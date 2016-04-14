package myapplication.mynewsapp.adapter;

/**
 * Created by ttslso on 2016/3/23.
 */
/*
public class NewsItemAdapter extends BaseAdapter {
    private List<Latest.StoriesEntity> storiesEntities;
    private Context context;
    private ImageLoader mImageloader;
    private DisplayImageOptions options;

    public NewsItemAdapter(Context context, List<Latest.StoriesEntity> items) {
        this.context = context;
        storiesEntities = items;
        mImageloader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public int getCount() {
        return storiesEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return storiesEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        String readSeq = PreUtils.getStringFromDefault(context, "read", "");
//        if (readSeq.contains(entities.get(position).getId() + "")) {
//            viewHolder.tv_title.setTextColor(context.getResources().getColor(R.color.clicked_tv_textcolor));
//        } else {
//            viewHolder.tv_title.setTextColor(context.getResources().getColor(isLight ? android.R.color.black : android.R.color.white));
//        }
//
//        ((LinearLayout) viewHolder.iv_title.getParent().getParent().getParent()).setBackgroundColor(context.getResources().getColor(isLight ? R.color.light_news_item : R.color.dark_news_item));
//        ((FrameLayout) viewHolder.tv_title.getParent().getParent()).setBackgroundResource(isLight ? R.drawable.item_background_selector_light : R.drawable.item_background_selector_dark);
        Latest.StoriesEntity entity = storiesEntities.get(position);
        viewHolder.tv_title.setText(entity.getTitle());
        if (entity.getImages() != null) {
            viewHolder.iv_title.setVisibility(View.VISIBLE);
            mImageloader.displayImage(entity.getImages().get(0), viewHolder.iv_title, options);
        } else {
            viewHolder.iv_title.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tv_title;
        ImageView iv_title;
    }

}*/
