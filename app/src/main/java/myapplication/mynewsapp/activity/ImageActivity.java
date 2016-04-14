package myapplication.mynewsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import myapplication.mynewsapp.fragment.FirstFragment;
import myapplication.mynewsapp.util.HttpUtils;
import myapplication.mynewsapp.R;
import myapplication.mynewsapp.util.Constant;

/**
 * Created by ttslso on 2016/3/18.
 */
public class ImageActivity extends Activity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //！！必须放在setContent之前！！
        setContentView(R.layout.image_activtiy);

        mImageView = (ImageView)findViewById(R.id.image_start);
        initImage();
    }

    private void initImage(){
        Log.d("before_dir","OK");
        File dir = getFilesDir();
        final File imageFile = new File(dir,"start.jpg");
        if (imageFile.exists()){
            mImageView.setImageBitmap(BitmapFactory
                    .decodeFile(imageFile.getAbsolutePath()));
        }else{
            mImageView.setImageResource(R.mipmap.start);
        }
        Log.d("before_animation","OK");
        //设置动画效果
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f
        , Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                HttpUtils.get(Constant.START, new AsyncHttpResponseHandler() {
                    @Override
                    //从知乎API获取启动动画的图片
                    //获取失效，使用缓存（已设置)图片作为动画图片
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(bytes));
                            String url = jsonObject.getString("img");
                            HttpUtils.get(url, new BinaryHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                    //更改缓存
                                    saveImage(imageFile, bytes);
                                    startActivity();
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, byte[] bytes,
                                                      Throwable throwable) {
                                    startActivity();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes,
                                          Throwable throwable) {
                        startActivity();
                    }
                });

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImageView.startAnimation(scaleAnimation);
    }

    private void startActivity(){
        Intent intent = new Intent(ImageActivity.this,FirstFragment.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        finish();
    }

    private void saveImage(File file,byte[] bytes){
        try{
            if(file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
