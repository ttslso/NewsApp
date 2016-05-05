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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.model.StoriesBean;

/**
 * Created by ttslso on 2016/4/3.
 */
public class ThemeNewsItemAdapter extends BaseAdapter {
    private List<StoriesBean> beans;
    private List<String> images;
    private Context context;
    public RequestQueue mQueue;
    private com.android.volley.toolbox.ImageLoader imageLoader;


    public ThemeNewsItemAdapter(Context context) {
        this.context = context;
        this.beans = new ArrayList<StoriesBean>();
        this.images = new ArrayList<String>();

        mQueue = Volley.newRequestQueue(context);
        imageLoader = new com.android.volley.toolbox.ImageLoader(mQueue, new BitmapCache() {

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

    public void addImage(List<String> iItems){
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
        try{
            ViewHolder viewHolder = null;
            if (view == null){
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.theme_item,viewGroup,false);
                viewHolder.textView = (TextView)view.findViewById(R.id.tv_title);
                viewHolder.imageView = (NetworkImageView)view.findViewById(R.id.iv_title);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }

            StoriesBean bean = beans.get(i);
            viewHolder.textView.setText(bean.getTitle());

            String image = images.get(i);
            viewHolder.imageView.setDefaultImageResId(R.drawable.load);
            viewHolder.imageView.setErrorImageResId(R.drawable.load);
            viewHolder.imageView.setImageUrl(image.toString(),imageLoader);
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    public static class ViewHolder {
        TextView textView;
        NetworkImageView imageView;
    }

    public class BitmapCache implements com.android.volley.toolbox.ImageLoader.ImageCache{

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
    /*private List<StoriesBean> beans;
    private Context context;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public ThemeNewsItemAdapter(Context context,List<StoriesEntity> items){
        this.context = context;
        entities = items;
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).build();
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
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.theme_item,parent,false);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StoriesEntity entity = entities.get(position);
        viewHolder.tv_title.setText(entity.getTitle());
        if (entity.getImages() != null){
            viewHolder.iv_title.setVisibility(View.VISIBLE);
            mImageLoader.displayImage(entity.getImages().get(0), viewHolder.iv_title, options);

        }else {
            viewHolder.iv_title.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static class ViewHolder{
        TextView tv_title;
        ImageView iv_title;
    }*/
}
