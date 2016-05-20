package myapplication.mynewsapp.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

import myapplication.mynewsapp.db.ListDBHelper;
import myapplication.mynewsapp.util.NetworkConn;
import myapplication.mynewsapp.R;
import myapplication.mynewsapp.request.MyStringRequest;
import myapplication.mynewsapp.activity.NewsActivity;
import myapplication.mynewsapp.adapter.NewsListAdapter;
import myapplication.mynewsapp.model.Before;
import myapplication.mynewsapp.model.Latest;
import myapplication.mynewsapp.model.StoriesBean;
import myapplication.mynewsapp.model.TopStoriesBean;
import myapplication.mynewsapp.util.Constant;
import myapplication.mynewsapp.view.ScrollView;
import myapplication.mynewsapp.view.VpSwipeRefreshLayout;

/**
 * Created by ttslso on 2016/4/9.
 */
public class MainNewsFragment extends Fragment {

    public static final int VALUE = 1;//数据库的value值
    private ListView lv_news;
    private ScrollView mScrollView;
    private Handler handler = new Handler();
    private String date;
    private NewsListAdapter mAdapter;
    private Boolean isLoading = false;
    //重写swiperefreshlayout中的触摸拦截事件
    //修复viewpager和swiperefreshlayout的冲突
    private VpSwipeRefreshLayout sr;//添加触摸拦截事件的下拉刷新
    private RequestQueue mQueue;
    private Gson gson;
    private Before before;
    private ListDBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , final Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.main_news_layout, container, false);
        lv_news = (ListView) view.findViewById(R.id.lv_news);

        dbHelper = new ListDBHelper(getActivity());

        //SwipeRefresh刷新控件
        //下拉刷新成功时重新载入fragment
        sr = (VpSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
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
                }, 2000);
            }
        });

        //mActivity = getActivity();
        //轮播控件scrollview,采用viewpager实现，作为listview的header
        View header = inflater.inflate(R.layout.scroll_view, lv_news, false);
        mScrollView = (ScrollView) header.findViewById(R.id.scrollview);
        //头部轮播点击事件
        mScrollView.setOnItemClickListener(new ScrollView.OnItemClickListener() {
            @Override
            public void click(View v, TopStoriesBean topBean) {
                //将view坐标x,y设置在屏幕正中
                int[] startLocation = new int[2];
                v.getLocationOnScreen(startLocation);
                startLocation[0] += v.getWidth() / 2;
                StoriesBean storiesBean = new StoriesBean();
                storiesBean.setId(topBean.getId());
                storiesBean.setTitle(topBean.getTitle());
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtra(Constant.START_LOCATION, startLocation);
                intent.putExtra("bean", storiesBean);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        lv_news.addHeaderView(header);
        mAdapter = new NewsListAdapter(getActivity());
        lv_news.setAdapter(mAdapter);
        //新闻内容点击事件
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startLocation = new int[2];
                view.getLocationOnScreen(startLocation);
                startLocation[0] += view.getWidth()/2;
                StoriesBean bean = (StoriesBean) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(),NewsActivity.class);
                intent.putExtra(Constant.START_LOCATION,startLocation);
                intent.putExtra("bean",bean);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });
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
                        //加载before
                        loadMore(Constant.BEFORE + date);
                    }
                }
            }
        });
        return view;
    }

    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        getData();
    }

    //添加缓存，当网络连接时候替换数据库中的url，未连接网络使用数据库之前存储的url
    //不能使用经过请求后的gson相应对象，此处数据存储功能需要更替
    protected void getData() {
        if(NetworkConn.isConnected(getActivity())){
            String url = Constant.LATESTNEWS;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.close();
            parseLatest(url);
        }else{
            Toast.makeText(getActivity(), "未连接网络", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }
    //上拉加载更多
    protected void loadMore(final String url) {
        isLoading = true;
        mQueue = Volley.newRequestQueue(getActivity());
        MyStringRequest request = new MyStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG_before", response);
                gson = new Gson();
                before = gson.fromJson(response, Before.class);
                if (before == null) {
                    isLoading = false;
                    return;
                }
                date = before.getDate();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<StoriesBean> beans = before.getStories();
                        mAdapter.addTitle(beans);
                        for (int j = 0; j < beans.size(); j++) {
                            List<String> images = beans.get(j).getImages();
                            mAdapter.addImage(images);
                        }
                        isLoading = false;
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(getActivity(), "未能读取更多", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(request);
    }
    //解析json
    public void parseLatest(String url){
        isLoading = true;
        mQueue = Volley.newRequestQueue(this.getActivity());
        MyStringRequest request = new MyStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG_latest", response);
                gson = new Gson();
                //对象为数组 需要response,new TypeToken..
                final Latest latest = gson.fromJson(response, Latest.class);
                date = latest.getDate();
                Log.d("TAG", latest.getDate());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<StoriesBean> beans = latest.getStories();
                        List<TopStoriesBean> topBeans = latest.getTop_stories();
                        mScrollView.setTopBeans(topBeans);
                        mAdapter.addTitle(beans);
                        for (int i = 0; i < beans.size(); i++) {
                            Log.d("TAG", beans.get(i).getTitle());
                            List<String> images = beans.get(i).getImages();
                            mAdapter.addImage(images);
                        }
                        isLoading = false;
                        sr.setRefreshing(false);
                    }
                });

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        Toast.makeText(getActivity(), "未能加载数据", Toast.LENGTH_SHORT).show();
                    }
                });
        mQueue.add(request);

    }

}



