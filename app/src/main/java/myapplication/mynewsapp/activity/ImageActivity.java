package myapplication.mynewsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.model.StartImage;
import myapplication.mynewsapp.util.Constant;

/**
 * Created by ttslso on 2016/3/18.
 */
public class ImageActivity extends Activity {

    private ImageView mImageView;
    private Gson gson;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //必须放在setContent之前
        setContentView(R.layout.image_activtiy);
        mImageView = (ImageView) findViewById(R.id.image_start);
        initImage();
    }

    private void initImage() {
        File dir = getFilesDir();
        final File imageFile = new File(dir, "start.jpg");
        if (imageFile.exists()) {
//            mImageView.setImageBitmap(BitmapFactory
//                    .decodeFile(imageFile.getAbsolutePath()));
            mImageView.setImageResource(R.mipmap.start);
        } else {
            mImageView.setImageResource(R.mipmap.start);
        }
        //设置动画效果
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(5000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    mQueue = Volley.newRequestQueue(ImageActivity.this);
                    StringRequest request = new StringRequest(Constant.START, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            gson = new Gson();
                            StartImage simage = gson.fromJson(response, StartImage.class);
                            byte[] bytes = simage.getImg().getBytes();
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

    private void saveImage(File file, byte[] bytes) {
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
