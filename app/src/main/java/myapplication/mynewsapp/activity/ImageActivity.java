package myapplication.mynewsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.request.MyStringRequest;
import myapplication.mynewsapp.model.StartImage;
import myapplication.mynewsapp.util.Constant;

/**
 * Created by ttslso on 2016/3/18.
 */
public class ImageActivity extends Activity {

    private ImageView mImageView;
    private Button mSkipBtn;
    private Gson gson;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_activtiy);

        //隐藏系统状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mImageView = (ImageView) findViewById(R.id.image_start);
        mSkipBtn = (Button)findViewById(R.id.skip);
        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
        initImage();
    }

    public void initImage(){
        final File imageFile = new File( getFilesDir(), "start.jpg");
        if (imageFile.exists()) {
            //bm return null,可能url存在问题，使用解析数据流方式更替
            Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            if (bm != null){
                mImageView.setImageBitmap(bm);
            }
            mImageView.setImageResource(R.mipmap.img2);
        } else {
            mImageView.setImageResource(R.mipmap.img2);
        }

        //设置动画效果
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(5000);
        //动画结束之后进行缓存url的jpg图片，缓存结束后结束当前活动
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    mQueue = Volley.newRequestQueue(ImageActivity.this);
                    MyStringRequest request = new MyStringRequest(Constant.START, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            gson = new Gson();
                            StartImage simage = gson.fromJson(response, StartImage.class);
                            String img = simage.getImg();
                            byte[] bytes = Base64.decode(img,Base64.DEFAULT);
                            saveImage(imageFile, bytes);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                        }
                    });
                    mQueue.add(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImageView.startAnimation(scaleAnimation);
    }

    private void startActivity() {
        Intent intent = new Intent(ImageActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        finish();
    }

    //image成功存储但是通过bitmap工厂返回为null
    private void saveImage(File file, byte[] bytes) {
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
