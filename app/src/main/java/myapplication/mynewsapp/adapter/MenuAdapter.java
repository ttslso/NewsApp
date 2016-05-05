package myapplication.mynewsapp.adapter;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.model.MenuItem;

/**
 * Created by ttslso on 2016/4/30.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> implements View.OnClickListener {

    private List<MenuItem> items;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public MenuAdapter(List<MenuItem> items) {
        this.items = items;
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onClick(View view) {
        if (onRecyclerItemClickListener != null) {
            //getTag获取item位置
            onRecyclerItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.textView.setText(item.getTitle());
        //API有限制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(item.getDrawable(), null, null, null);
        }
        //setTag传递item位置
        holder.itemView.setTag(position);
        holder.itemView.setClickable(true);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    //提供外部引用的监视器
    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        this.onRecyclerItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item);
        }

    }
    //自定义接口
    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }
}
