package myapplication.mynewsapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.model.StoriesBean;

/**
 * Created by ttslso on 2016/4/30.
 */
public class NewsListAdapter extends BaseAdapter {

    private List<StoriesBean> beans;
    private List<String> images;
    private Context context;
    public RequestQueue mQueue;
    private ImageLoader imageLoader;

    public NewsListAdapter(Context context) {
        this.context = context;
        this.beans = new ArrayList<StoriesBean>();
        this.images = new ArrayList<String>();

        mQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(mQueue, new BitmapCache() {

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });
    }

    public void addTitle(List<StoriesBean> tItems) {
        this.beans.addAll(tItems);
        notifyDataSetChanged();
    }

    public void addImage(List<String> iItems) {
        this.images.addAll(iItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int i) {
        return beans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.main_news_item,viewGroup,false);
            viewHolder.textView = (TextView)view.findViewById(R.id.tv_title);
            viewHolder.imageView = (NetworkImageView)view.findViewById(R.id.iv_title);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        StoriesBean bean = beans.get(i);
        String image = images.get(i);

        viewHolder.textView.setText(bean.getTitle());
        viewHolder.imageView.setDefaultImageResId(R.drawable.load);
        viewHolder.imageView.setErrorImageResId(R.drawable.load);
        viewHolder.imageView.setImageUrl(image.toString(),imageLoader);

        return view;
    }

    public static class ViewHolder {
        TextView textView;
        NetworkImageView imageView;
    }

    public class BitmapCache implements ImageLoader.ImageCache{

        public LruCache<String,Bitmap> mCache;

        public BitmapCache(){
            int maxSize = 10*1024*1024;
            mCache = new LruCache<String, Bitmap>(maxSize){
                protected int sizeOf(String key,Bitmap bitmap)
                {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url,bitmap);
        }
    }
}
