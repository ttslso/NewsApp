package myapplication.mynewsapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.util.Constant;
import myapplication.mynewsapp.model.StoriesEntity;

/**
 * Created by ttslso on 2016/3/26.
 */

//MainFragment中处理内容的适配器
public class MainNewsItemAdapter extends BaseAdapter {

    private List<StoriesEntity> entities;
    private Context context;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public MainNewsItemAdapter(Context context){
        this.context = context;
        this.entities = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    //单个实体添加进list中
    public void addList(List<StoriesEntity> items){
        this.entities.addAll(items);
        //刷新后的动画效果
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.main_news_item ,parent,false);
            viewHolder.tv_topic = (TextView)convertView.findViewById(R.id.tv_topic);//topic
            viewHolder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
            viewHolder.iv_title = (ImageView)convertView.findViewById(R.id.iv_title);
            //将缓存保存
            convertView.setTag(viewHolder);
        }else{

            viewHolder = (ViewHolder) convertView.getTag();
        }
        //从list中获取单个实体
        StoriesEntity entity = entities.get(position);
        if (entity.getType() == Constant.TOPIC){
            //获取的实例为在ScrollView中显示的
            ((FrameLayout) viewHolder.tv_topic.getParent()).setBackgroundColor(Color.TRANSPARENT);
            viewHolder.tv_title.setVisibility(View.GONE);//不显示标题
            viewHolder.iv_title.setVisibility(View.GONE);//不显示图片
            viewHolder.tv_topic.setVisibility(View.VISIBLE);//显示topic
            viewHolder.tv_topic.setText(entity.getTitle());//topic上显示标题
        }else{
            ((FrameLayout)viewHolder.tv_topic.getParent()).setBackgroundColor(Color.TRANSPARENT);
            viewHolder.tv_topic.setVisibility(View.GONE);
            viewHolder.tv_title.setVisibility(View.VISIBLE);
            viewHolder.iv_title.setVisibility(View.VISIBLE);
            viewHolder.tv_title.setText(entity.getTitle());
            mImageLoader.displayImage(entity.getImages().get(0),viewHolder.iv_title,options);
        }
        return convertView;
    }

    //缓存
    public static class ViewHolder{
        TextView tv_topic;
        TextView tv_title;
        ImageView iv_title;
    }
}
