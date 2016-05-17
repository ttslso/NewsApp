package myapplication.mynewsapp.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import myapplication.mynewsapp.util.NetworkConn;
import myapplication.mynewsapp.R;
import myapplication.mynewsapp.request.MyStringRequest;
import myapplication.mynewsapp.db.WebContentDBHelper;
import myapplication.mynewsapp.animation.RevealBackgroundView;
import myapplication.mynewsapp.model.Content;
import myapplication.mynewsapp.model.StoriesBean;
import myapplication.mynewsapp.util.Constant;

/**
 * Created by ttslso on 2016/3/29.
 */
public class NewsActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    private RevealBackgroundView mRevealBackgroundView;
    private WebView mWebView;
    private Content content;
    private StoriesBean bean;
    private ImageView iv;
    private AppBarLayout mAppBarLayout;
    private RequestQueue mQueue;
    private Handler handler = new Handler();
    private Gson gson;
    private WebContentDBHelper dbHelper;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;


    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.web_view_layout);
        dbHelper = new WebContentDBHelper(NewsActivity.this);
        initImgLoader();
        initUI();
        setupRevealBackground(saveInstanceState);
    }

    private void initImgLoader(){
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private void initUI() {
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
        mRevealBackgroundView = (RevealBackgroundView) findViewById(R.id.revealBackground);

        bean = (StoriesBean) getIntent().getSerializableExtra("bean");
        iv = (ImageView) findViewById(R.id.iv);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mAppBarLayout.setVisibility(View.INVISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar左上角加上返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //伸缩工具栏
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setTitle(bean.getTitle());

        initWebView();
        getData();
    }
    //初始化webview中的设置
    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings settings = mWebView.getSettings();
        //支持js
        settings.setJavaScriptEnabled(true);
        //设置加载缓存模式
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //开启Dom storage
        settings.setDomStorageEnabled(true);
        //开启data storage
        settings.setDatabaseEnabled(true);
        //开启HTML缓存
        settings.setAppCacheEnabled(true);
    }

    public void getData() {
        if (NetworkConn.isConnected(NewsActivity.this)) {
            String url = Constant.CONTENT + bean.getId();
            url = url.replaceAll("'", "'");
            parseContent(url);
        }else{
            //未连接网络时，读取SQLite中的response
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from WebContent where newsId ="+bean.getId(),null);
            if (cursor.moveToFirst()){
                Toast.makeText(this,"未连接网络,读取数据库的缓存",Toast.LENGTH_SHORT).show();
                String response = cursor.getString(cursor.getColumnIndex("json"));
                try{
                    gson = new Gson();
                    content = gson.fromJson(response, Content.class);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                iv.setImageResource(R.mipmap.img1);
                                String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
                                String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
                                html = html.replace("<div class=\"img-place-holder\">", "");
                                mWebView.loadDataWithBaseURL("x-data://base",html,"text/html","UTF-8", null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (RuntimeException e){
                    Toast.makeText(this,"image cache error",Toast.LENGTH_LONG).show();//含有图片的html无法读取
                }
            }else{
                Toast.makeText(this,"未连接网络",Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            db.close();
        }
    }

    public void parseContent(String url) {
        mQueue = Volley.newRequestQueue(this);
        MyStringRequest request = new MyStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //写入Sqlite
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("newsId",bean.getId());
                cv.put("json",response);
                db.insert("WebContent",null,cv);
                db.close();
                gson = new Gson();
                content = gson.fromJson(response, Content.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            imageLoader.displayImage(content.getImage(), iv, options);
                            String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
                            String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
                            html = html.replace("<div class=\"img-place-holder\">", "");
                            //"<style>img{display: inline;height: auto;max-width: 100%;}</style>" 解决图片自适应问题
                            String data ="<style>img{display: inline;height: auto;max-width: 100%;}</style>" + html;
                            mWebView.loadDataWithBaseURL("x-data://base",data,"text/html","UTF-8", null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(request);
    }

    //设置revealbackground动画效果
    public void setupRevealBackground(Bundle saveInstanceState) {
        mRevealBackgroundView.setOnStateChangeListener(this);
        if (saveInstanceState == null) {
            Log.d("TAG", "saveInstanceState=null");
            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
            mRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRevealBackgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            mRevealBackgroundView.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            mAppBarLayout.setVisibility(View.VISIBLE);
        }
    }
    //回退结束当前活动
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

}

