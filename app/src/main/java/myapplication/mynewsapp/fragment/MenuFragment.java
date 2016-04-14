package myapplication.mynewsapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.util.HttpUtils;
import myapplication.mynewsapp.model.NewsListItem;
import myapplication.mynewsapp.R;
import myapplication.mynewsapp.util.Constant;


/**
 * Created by ttslso on 2016/3/19.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    private Button mButton;
    private TextView drawer_tv,test_tv;

    private RecyclerView mRyView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    //private View view;

    private List<NewsListItem> items;
    private Handler handler = new Handler();

    private ListView lv_item;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle
                               saveInstanceState){
        View view = inflater.inflate(R.layout.menu_fglayout, container, false);

        mButton = (Button)view.findViewById(R.id.login_button);
        mButton.setOnClickListener(this);

        drawer_tv = (TextView)view.findViewById(R.id.drawer_tv);
        drawer_tv.setOnClickListener(this);

        mRyView = (RecyclerView)view.findViewById(R.id.rv_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        //设置具体的布局管理器
        mLayoutManager.setOrientation(mLayoutManager.VERTICAL);
        mRyView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsTypeAdapter();
        mRyView.setAdapter(mAdapter);
        getItems();
        return view;
    }

    //适配器在getItem方法中实现
    //通过gson解析知乎API获取DrawerLayout中的theme列表数据
    private void getItems(){
        items = new ArrayList<NewsListItem>();
        //HttpUtils已封装好的API类
        HttpUtils.get(Constant.THEMES, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    //获取数据并设置
                    JSONArray itemArray = response.getJSONArray("others");
                    for (int i = 0; i < itemArray.length(); i++) {
                        NewsListItem newsListItem = new NewsListItem();
                        JSONObject itemObject = itemArray.getJSONObject(i);
                        newsListItem.setTitle(itemObject.getString("name"));
                        newsListItem.setId(itemObject.getString("id"));
                        //将NewsListItem数据添加到items中
                        items.add(newsListItem);
                    }
                    //线程中的post
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //通过适配器管理数据显示
                            mRyView.setAdapter(mAdapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public class NewsTypeAdapter extends RecyclerView.Adapter<NewsTypeAdapter.ViewHolder> {

        @Override
        //渲染items中的每一个view，将view封装在ViewHolder中
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.menu_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
        //渲染数据到View中
        //与getView相似
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.item_text_view.setText(items.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
        //给list中快速设置值，提升性能
        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView item_text_view;
            public ViewHolder(View view){
                super(view);
                item_text_view = (TextView)view.findViewById(R.id.tv_item);
            }
        }
    }

    @Override
    public void onClick(View v){

    }
    /*
    **********************采用ListView**********************************

    public void initListView{
        lv_item = (ListView) view.findViewById(R.id.lv_item);
    }
    *******************************************************************
    public void getItems(){
        items = new ArrayList<NewsListItem>();
        HttpUtils.get(Constant.THEMES, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray itemsArray = response.getJSONArray("others");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        NewsListItem newsListItem = new NewsListItem();
                        JSONObject itemObject = itemsArray.getJSONObject(i);
                        newsListItem.setTitle(itemObject.getString("name"));
                        newsListItem.setId(itemObject.getString("id"));
                        items.add(newsListItem);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            lv_item.setAdapter(new NewsTypeAdapter());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
********************************ListView中的通用适配器*******************************************
    public class NewsTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.menu_item, parent, false);
            }
            TextView tv_item = (TextView) convertView
                    .findViewById(R.id.tv_item);
            tv_item.setText(items.get(position).getTitle());
            return convertView;
        }
    }
    */
}


