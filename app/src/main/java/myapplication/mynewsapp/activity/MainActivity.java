package myapplication.mynewsapp.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.fragment.NewsFragment;
import myapplication.mynewsapp.fragment.ProjectFragment;
import myapplication.mynewsapp.fragment.VideoFragment;

/**
 * Created by ttslso on 2016/4/8.
 */
public class MainActivity extends FragmentActivity {
    private FrameLayout container;
    private RadioGroup mRadioGp;
    private RadioButton mNewsbtn;
    private RadioButton mVideobtn;
    private RadioButton mPrjbtn;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;

    static final int NUM_ITEMS = 3;//底部tab数3

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    public void initUI() {
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
        container = (FrameLayout) findViewById(R.id.container);

        mRadioGp = (RadioGroup) findViewById(R.id.radio_group);
        mNewsbtn = (RadioButton) findViewById(R.id.news_radio_button);
        mVideobtn = (RadioButton) findViewById(R.id.video_radio_button);
        mPrjbtn = (RadioButton) findViewById(R.id.pject_radio_button);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer
                , toolbar, R.string.app_name, R.string.app_name);
        mDrawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //底部button的点击事件
        mRadioGp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.news_radio_button:
                        index = 0;
                        break;
                    case R.id.video_radio_button:
                        index = 1;
                        break;
                    case R.id.pject_radio_button:
                        index = 2;
                        break;
                }
                Fragment fragment = (Fragment) rfAdapter.instantiateItem(container, index);
                rfAdapter.setPrimaryItem(container, 0, fragment);
                rfAdapter.finishUpdate(container);
            }
        });
    }

    protected void onStart(){
        super.onStart();
        //默认加载第一项tab
        mRadioGp.check(R.id.news_radio_button);
    }

    FragmentStatePagerAdapter rfAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0:
                    fragment = new NewsFragment();
                    break;
                case 1:
                    fragment = new VideoFragment();
                    break;
                case 2:
                    fragment = new ProjectFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    };
}


