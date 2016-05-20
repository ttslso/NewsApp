package myapplication.mynewsapp.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.adapter.NewsFragmentAdapter;


/**
 * Created by ttslso on 2016/3/16.
 */
public class NewsFragment extends Fragment {

    private ViewPager mViewPager; //仿网易滑动page
    private TabLayout mTabLayout;//Tab导航栏
    private NewsFragmentAdapter mNewsFragmentAdapter;

    private List<Fragment> list_fragment;
    private List<String>   list_title;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment,null);
        mTabLayout = (TabLayout) view.findViewById(R.id.page_tab_layout);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager = (ViewPager) view.findViewById(R.id.main_view_pager);
        fragmentChange();
        return view;
    }

    public void fragmentChange(){
        list_fragment = new ArrayList<>();

        MainNewsFragment mFragment = new MainNewsFragment();
        ThemeFragment sFragment = new ThemeFragment().newInstance(11);
        ThemeFragment tFragment = new ThemeFragment().newInstance(10);
        ThemeFragment fFragment = new ThemeFragment().newInstance(9);
        ThemeFragment vFragment = new ThemeFragment().newInstance(8);

        list_fragment.add(mFragment);
        list_fragment.add(sFragment);
        list_fragment.add(tFragment);
        list_fragment.add(fFragment);
        list_fragment.add(vFragment);

        list_title = new ArrayList<>();
        list_title.add("每日热闻");
        list_title.add("趣闻日报");
        list_title.add("互联网安全");
        list_title.add("动漫日报");
        list_title.add("体育日报");

        mNewsFragmentAdapter = new NewsFragmentAdapter(getFragmentManager(),
                list_fragment, list_title);
        mViewPager.setAdapter(mNewsFragmentAdapter);
        //Tab与ViewPager连接
        mTabLayout.setupWithViewPager(mViewPager);
    }

    //解决叠层问题
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
}
