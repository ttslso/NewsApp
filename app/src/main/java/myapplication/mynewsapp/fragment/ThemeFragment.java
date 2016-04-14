package myapplication.mynewsapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.adapter.ThemeNewsItemAdapter;
import myapplication.mynewsapp.activity.NewsContentActivity;
import myapplication.mynewsapp.model.StoriesEntity;
import myapplication.mynewsapp.model.Theme;
import myapplication.mynewsapp.util.Constant;
import myapplication.mynewsapp.util.HttpUtils;

/**
 * Created by ttslso on 2016/4/2.
 */
public class ThemeFragment extends Fragment {

    private ListView slv_news;
    private ImageLoader mImageLoader;
    private int urlId;
    private ImageView iv_title;
    private TextView tv_title;
    private Theme theme;
    private Activity mActivity ;
    private DisplayImageOptions options;
    private SwipeRefreshLayout sr;

    public ThemeFragment(int id){
        urlId = id;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.theme_fragment_layout, container, false);
        mActivity = getActivity();
        slv_news = (ListView) view.findViewById(R.id.s_lv_news);
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        View header = LayoutInflater.from(getContext()).inflate(R.layout.s_head_view,slv_news,false);
        iv_title = (ImageView)header.findViewById(R.id.iv_title);
        tv_title = (TextView) header.findViewById(R.id.tv_title);
        slv_news.addHeaderView(header);

        //SwipeRefresh刷新控件
        sr = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshFragment();
                sr.setRefreshing(false);
            }
        });

        initListener();
        return view;
    }

    public void initListener() {
        slv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startLocation = new int[2];
                view.getLocationOnScreen(startLocation);
                startLocation[0] += view.getWidth()/2;

                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity,NewsContentActivity.class);
                intent.putExtra(Constant.START_LOCATION,startLocation);
                intent.putExtra("entity",entity);
                startActivity(intent);
                mActivity.overridePendingTransition(0,0);
            }
        });

        slv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (slv_news != null && slv_news.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
//                    ((FirstFragment) mActivity).setSwipeRefreshEnable(enable);

                    //在顶部时才能刷新
                    sr.setEnabled(enable);
                }
            }
        });
    }

    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        initData();
    }

    protected void initData(){
        HttpUtils.get(Constant.THEMENEWS+urlId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                theme = gson.fromJson(responseString,Theme.class);
                tv_title.setText(theme.getDescription());
                mImageLoader.displayImage(theme.getImage(), iv_title, options);
                ThemeNewsItemAdapter mAdapter = new ThemeNewsItemAdapter(mActivity,theme.getStories());
                slv_news.setAdapter(mAdapter);
            }
        });
    }
}

