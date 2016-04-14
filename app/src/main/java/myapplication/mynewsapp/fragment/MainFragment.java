package myapplication.mynewsapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.activity.NewsContentActivity;
import myapplication.mynewsapp.adapter.MainNewsItemAdapter;
import myapplication.mynewsapp.model.Before;
import myapplication.mynewsapp.model.Latest;
import myapplication.mynewsapp.model.StoriesEntity;
import myapplication.mynewsapp.util.Constant;
import myapplication.mynewsapp.util.HttpUtils;
import myapplication.mynewsapp.view.ScrollView;
import myapplication.mynewsapp.view.VpSwipeRefreshLayout;

/**
 * Created by ttslso on 2016/4/9.
 */
public class MainFragment extends Fragment {
    private ListView lv_news;
    private List<Latest> items;
    private Latest latest;
    private Before before;
    private ScrollView mScrollView;
    private Handler handler = new Handler();
    private Activity mActivity;
    private String date;
    private MainNewsItemAdapter mAdapter;
    private Boolean isLoading = false;
    // private SwipeRefreshLayout sr;
    //重写swiperefreshlayout中的触摸拦截事件
    //修复viewpager和swiperefreshlayout的冲突
    private VpSwipeRefreshLayout sr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , final Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.main_news_layout, container, false);
        lv_news = (ListView) view.findViewById(R.id.lv_news);

        //SwipeRefresh刷新控件
        //下拉刷新成功时重新载入fragment
        sr = (VpSwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        //第一次加载时刷新
        sr.post(new Runnable() {
            @Override
            public void run() {
                sr.setRefreshing(true);
            }
        });
        //刷新时重新加载数据
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                },2000);
            }
        });

        mActivity = getActivity();
        //轮播控件scrollview,实质为viewpager，作为listview的header
        View header = inflater.inflate(R.layout.scroll_view, lv_news, false);
        mScrollView = (ScrollView) header.findViewById(R.id.scrollview);
        //头部的轮播的点击事件
        mScrollView.setOnItemClickListener(new ScrollView.OnItemClickListener() {
            @Override
            public void click(View v, Latest.TopStoriesEntity entity) {
                //将view坐标x,y设置在屏幕正中
                int[] startLocation = new int[2];
                v.getLocationOnScreen(startLocation);
                startLocation[0] += v.getWidth()/2;

                StoriesEntity storiesEntity = new StoriesEntity();
                storiesEntity.setId(entity.getId());
                storiesEntity.setTitle(entity.getTitle());
                Intent intent = new Intent(mActivity,NewsContentActivity.class);
                intent.putExtra(Constant.START_LOCATION,startLocation);
                intent.putExtra("entity",storiesEntity);
                startActivity(intent);
                mActivity.overridePendingTransition(0,0);
            }
        });
        lv_news.addHeaderView(header);
        mAdapter = new MainNewsItemAdapter(mActivity);
        lv_news.setAdapter(mAdapter);

        //解决swiperefresh下拉刷新和listview的滑动冲突
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem
                    , int visibleItemCount, int totalItemCount) {
                if (lv_news != null && lv_news.getChildCount() > 0) {
                    //listview在划动到顶部时才能下拉刷新
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    sr.setEnabled(enable);
                    if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                        //URL   http://news.at.zhihu.com/api/4/news/before/20131119
                        loadMore(Constant.BEFORE + date);
                    }
                }
            }
        });
        //新闻内容点击事件
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startLocation = new int[2];
                view.getLocationOnScreen(startLocation);
                startLocation[0] += view.getWidth() / 2;
                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity, NewsContentActivity.class);
                intent.putExtra(Constant.START_LOCATION, startLocation);
                intent.putExtra("entity", entity);
                startActivity(intent);
                mActivity.overridePendingTransition(0, 0);
            }
        });

        return view;
    }

    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        initData();
    }

    protected void initData(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 2000);
    }

    protected void getData() {
        isLoading = true;
        HttpUtils.get(Constant.LATESTNEWS, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                latest = gson.fromJson(responseString, Latest.class);
                date = latest.getDate();
                mScrollView.setTopEntities(latest.getTop_stories());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //已经将StoriesEntity单独封装，Latest和Before能通用
                        List<StoriesEntity> storiesEntities = latest.getStories();
                        StoriesEntity topic = new StoriesEntity();
                        topic.setType(Constant.TOPIC);
                        topic.setTitle("今日热闻");
                        storiesEntities.add(0, topic);
                        mAdapter.addList(storiesEntities);
                        isLoading = false;
                        sr.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void loadMore(final String url) {

        isLoading = true;
        HttpUtils.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getContext(), "load failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Toast.makeText(getContext(), "load success", Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                before = gson.fromJson(responseString, Before.class);
                if (before == null) {
                    isLoading = false;
                    return;
                }
                date = before.getDate();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //经过Gson解析的Before，获取实体list
                        List<StoriesEntity> storiesEntities = before.getStories();
                        StoriesEntity topic = new StoriesEntity();//topic为list中的单个实体
                        topic.setType(Constant.TOPIC);
                        topic.setTitle("旧热回顾");
                        storiesEntities.add(0, topic);
                        mAdapter.addList(storiesEntities);
                        isLoading = false;
                    }
                });
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

}
