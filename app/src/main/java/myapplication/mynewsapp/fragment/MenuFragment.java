package myapplication.mynewsapp.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.adapter.MenuAdapter;
import myapplication.mynewsapp.model.MenuItem;


/**
 * Created by ttslso on 2016/3/19.
 */
public class MenuFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRcView;
    private LinearLayoutManager mLayoutManager;
    private List<MenuItem> items;
    private MenuAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            saveInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);

        mRcView = (RecyclerView) view.findViewById(R.id.rv_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        //布局管理
        mLayoutManager.setOrientation(mLayoutManager.VERTICAL);
        mRcView.setLayoutManager(mLayoutManager);

        //List<> 添加item
        initItems(getActivity());

        mAdapter = new MenuAdapter(items);
        mRcView.setAdapter(mAdapter);

        mAdapter.setOnRecyclerItemClickListener(new MenuAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        return view;
    }

    public void initItems(Context context) {
        items = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            items.add(new MenuItem("我的消息", context.getResources().getDrawable(R.drawable.ic_toggle_star, null)));
            items.add(new MenuItem("我的钱包", context.getResources().getDrawable(R.drawable.ic_toggle_star, null)));
            items.add(new MenuItem("离线阅读", context.getResources().getDrawable(R.drawable.ic_toggle_star, null)));
            items.add(new MenuItem("活动广场", context.getResources().getDrawable(R.drawable.ic_toggle_star, null)));
            items.add(new MenuItem("游戏中心", context.getResources().getDrawable(R.drawable.ic_toggle_star, null)));
            items.add(new MenuItem("我的邮箱", context.getResources().getDrawable(R.drawable.ic_toggle_star, null)));
        } else {
            Toast.makeText(getActivity(), "API版本过低", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
    }
}


