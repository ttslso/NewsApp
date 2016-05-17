package myapplication.mynewsapp.fragment;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.request.MyStringRequest;
import myapplication.mynewsapp.activity.NewsActivity;
import myapplication.mynewsapp.adapter.ThemeNewsItemAdapter;
import myapplication.mynewsapp.model.StoriesBean;
import myapplication.mynewsapp.model.Theme;
import myapplication.mynewsapp.util.Constant;

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
    private DisplayImageOptions options;
    private SwipeRefreshLayout sr;
    private RequestQueue mQueue;
    private Gson gson;
    private Handler handler = new Handler();
    private ThemeNewsItemAdapter mAdapter;

    public ThemeFragment newInstance(int urlId) {
        ThemeFragment newFragment = new ThemeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("urlId", urlId);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.theme_fragment_layout, container, false);
        slv_news = (ListView) view.findViewById(R.id.s_lv_news);
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.s_head_view, slv_news, false);
        iv_title = (ImageView) header.findViewById(R.id.iv_title);
        tv_title = (TextView) header.findViewById(R.id.tv_title);

        slv_news.addHeaderView(header);
        mAdapter = new ThemeNewsItemAdapter(getActivity());
        slv_news.setAdapter(mAdapter);
        initListener();

        //SwipeRefresh刷新控件
        sr = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //第一次加载时刷新
        sr.post(new Runnable() {
            @Override
            public void run() {
                sr.setRefreshing(true);
            }
        });
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

        return view;
    }

    public void initListener() {
        slv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        slv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (slv_news != null && slv_news.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    //((FirstFragment) mActivity).setSwipeRefreshEnable(enable);
                    //在顶部时才能刷新
                    sr.setEnabled(enable);
                }
            }
        });
    }

    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            urlId = args.getInt("urlId");
        }

        getData();
    }

    protected void getData() {
        try {
            mQueue = Volley.newRequestQueue(getActivity());
            MyStringRequest request = new MyStringRequest(Constant.THEMENEWS + urlId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("TAG_theme", response);
                        gson = new Gson();
                        //对象为数组 则需要response,new TypeToken..
                        theme = gson.fromJson(response, Theme.class);
                        tv_title.setText(theme.getDescription());
                        mImageLoader.displayImage(theme.getImage(), iv_title, options);
                        List<StoriesBean> beans = theme.getStories();
                        mAdapter.addTitle(beans);
                        for (int i = 0; i < beans.size(); i++) {
                            List<String> images = beans.get(i).getImages();
                            mAdapter.addImage(images);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sr.setRefreshing(false);
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

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "错误信息" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}

