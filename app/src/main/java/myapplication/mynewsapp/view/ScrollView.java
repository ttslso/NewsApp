package myapplication.mynewsapp.view;


import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

import myapplication.mynewsapp.R;
import myapplication.mynewsapp.model.TopStoriesBean;


/**
 * Created by ttslso on 2016/3/20.
 */
public class ScrollView extends FrameLayout implements OnClickListener {

    private List<TopStoriesBean> topStoriesBeans;
    private ImageLoader mImageLoader;
    private List<View> views;
    private Context context;
    private ViewPager vp;
    private boolean isAutoPlay;
    private int currentItem;
    private int delayTime;
    private LinearLayout ll_dot;
    private List<ImageView> iv_dots;
    private Handler handler = new Handler();
    private OnItemClickListener mItemClickListener;
    private DisplayImageOptions options;

    public ScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollView(Context context) {
        this(context, null);
    }
    /**
     * scroll images and titles as header of listview
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.topStoriesBeans = new ArrayList<>();
        initImageLoader(context);
        views = new ArrayList<View>();
        iv_dots = new ArrayList<ImageView>();
        delayTime = 2000;
    }

    public void setTopBeans(List<TopStoriesBean> topBeans) {
        this.topStoriesBeans = topBeans;
        reset();
    }

    private void reset() {
        views.clear();
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.scroll_view_layout,this,true);
        vp = (ViewPager) view.findViewById(R.id.vp);
        ll_dot = (LinearLayout) view.findViewById(R.id.ll_dot);
        ll_dot.removeAllViews();
        int len = topStoriesBeans.size();
        for (int i = 0; i < len; i++) {
            ImageView iv_dot = new ImageView(context);
            //Layout属性，变更高，宽,间距设置
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            ll_dot.addView(iv_dot, params);
            iv_dots.add(iv_dot);
        }
        //轮播控件机制，当i=len+1返回第一张图片 之后进行对i进行重置
        for (int i = 0; i <= len + 1; i++) {
            //布局中子项是放在前一项上的
            View fm = LayoutInflater.from(context).inflate(
                    R.layout.scroll_view_content_layout,null);
            ImageView iv = (ImageView) fm.findViewById(R.id.iv_title_s);
            TextView tv_title = (TextView) fm.findViewById(R.id.tv_title_s);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0) {
                mImageLoader.displayImage(topStoriesBeans.get(len - 1).getImage(),iv,options);
                tv_title.setText(topStoriesBeans.get(len - 1).getTitle());
            } else if (i == len + 1) {
                mImageLoader.displayImage(topStoriesBeans.get(0).getImage(), iv,options);
                tv_title.setText(topStoriesBeans.get(0).getTitle());
            } else {
                mImageLoader.displayImage(topStoriesBeans.get(i - 1).getImage(), iv,options);
                tv_title.setText(topStoriesBeans.get(i - 1).getTitle());
            }
            fm.setOnClickListener(this);
            views.add(fm);
        }
        vp.setAdapter(new MyPagerAdapter());
        vp.setFocusable(true);
        vp.setCurrentItem(1);
        currentItem = 1;
        vp.addOnPageChangeListener(new MyOnPageChangeListener());
        startPlay();
    }

    private void startPlay() {
        isAutoPlay = true;
        handler.postDelayed(task, 4000);
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                currentItem = currentItem % (topStoriesBeans.size() + 1) + 1;
                if (currentItem == 1) {
                    vp.setCurrentItem(currentItem, false);
                    handler.post(task);
                } else {
                    vp.setCurrentItem(currentItem);
                    handler.postDelayed(task, 5000);
                }
            } else {
                handler.postDelayed(task, 5000);
            }
        }
    };

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (vp.getCurrentItem() == 0) {
                        vp.setCurrentItem(topStoriesBeans.size(), false);
                    } else if (vp.getCurrentItem() == topStoriesBeans.size() + 1) {
                        vp.setCurrentItem(1, false);
                    }
                    currentItem = vp.getCurrentItem();
                    isAutoPlay = true;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < iv_dots.size(); i++) {
                if (i == arg0 - 1) {
                    iv_dots.get(i).setImageResource(R.drawable.dot_focus);
                } else {
                    iv_dots.get(i).setImageResource(R.drawable.dot_blur);
                }
            }

        }

    }


    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    //item点击事件接口
    public interface OnItemClickListener {
        void click(View v,TopStoriesBean bean);
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            TopStoriesBean bean = topStoriesBeans.get(vp.getCurrentItem() - 1);
            mItemClickListener.click(v,bean);
        }
    }

    public void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        mImageLoader = ImageLoader.getInstance();
    }
}

