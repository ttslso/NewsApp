package myapplication.mynewsapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.fragment.FirstFragment;
import myapplication.mynewsapp.fragment.SecondFragment;
import myapplication.mynewsapp.fragment.ThirdFragment;

/**
 * Created by ttslso on 2016/4/8.
 */
public class MainActivity extends FragmentActivity {
    private FrameLayout mFrameLayout;
    private RadioGroup mRadioGroup;
    private RadioButton newsButton;
    private RadioButton videoButton;
    private RadioButton pjectButton;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;//侧滑菜单

    static final int NUM_ITEMS = 3;//底部tab数3

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    public void initUI() {
        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        newsButton = (RadioButton) findViewById(R.id.news_radio_button);
        videoButton = (RadioButton) findViewById(R.id.video_radio_button);
        pjectButton = (RadioButton) findViewById(R.id.pject_radio_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout
                , toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
                Fragment fragment = (Fragment) rfAdapter.instantiateItem(mFrameLayout, index);
                rfAdapter.setPrimaryItem(mFrameLayout, 0, fragment);
                rfAdapter.finishUpdate(mFrameLayout);
            }
        });
    }

    protected void onStart(){
        super.onStart();
        //默认加载第一项tab
        mRadioGroup.check(R.id.news_radio_button);
    }

    FragmentStatePagerAdapter rfAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0:
                    fragment = new FirstFragment();
                    break;
                case 1:
                    fragment = new SecondFragment();
                    break;
                case 2:
                    fragment = new ThirdFragment();
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


