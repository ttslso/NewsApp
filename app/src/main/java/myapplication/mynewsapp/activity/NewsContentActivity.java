package myapplication.mynewsapp.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.animation.RevealBackgroundView;
import myapplication.mynewsapp.model.Content;
import myapplication.mynewsapp.model.StoriesEntity;
import myapplication.mynewsapp.util.Constant;
import myapplication.mynewsapp.util.HttpUtils;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by ttslso on 2016/3/29.
 */
public class NewsContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

    private RevealBackgroundView mRevealBackgroundView;
    private WebView mWebView;
    private Content content;
    private StoriesEntity entity;
    private ImageView iv;
    private AppBarLayout mAppBarLayout;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.web_view_layout);
        initUI();
        setupRevealBackground(saveInstanceState);
    }

    private void initUI() {
        mRevealBackgroundView = (RevealBackgroundView) findViewById(R.id.revealBackground);
        //putExtra("entity",entity);
        entity = (StoriesEntity) getIntent().getSerializableExtra("entity");

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
        mCollapsingToolbarLayout.setTitle(entity.getTitle());


        initWebView();
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);
        //支持js
        mWebView.getSettings().setJavaScriptEnabled(true);
        //关闭webview中缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //开启Dom storage
        mWebView.getSettings().setDomStorageEnabled(true);
        //开启data storage
        mWebView.getSettings().setDatabaseEnabled(true);
        //开启HTML5缓存
        mWebView.getSettings().setAppCacheEnabled(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        HttpUtils.get(Constant.CONTENT + entity.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(NewsContentActivity.this, "未获得数据", LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                responseString = responseString.replaceAll("'", "'");
                parseJson(responseString);
            }
        });
    }

    private void parseJson(String responseString) {
        Gson gson = new Gson();
        content = gson.fromJson(responseString, Content.class);
        //UIL框架使用
        final ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(content.getImage(), iv, options);
        //HTML解析
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }
}

