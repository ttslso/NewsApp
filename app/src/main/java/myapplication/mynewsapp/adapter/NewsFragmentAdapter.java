package myapplication.mynewsapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ttslso on 2016/4/2.
 */
public class NewsFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> list_fragment;
    private List<String> list_title;

   /* private Context context;
    //显示tab前的图片
    private int[] tabImg;*/

    public NewsFragmentAdapter(FragmentManager fm,List<Fragment> list_fragment,List<String> list_title){
        super(fm);
        this.list_fragment = list_fragment;
        this.list_title = list_title;
    }

    @Override
    public Fragment getItem(int i) {
        return list_fragment.get(i);
    }

    @Override
    public int getCount() {
        return list_fragment.size();
    }

    //可在tab前增加icon
    public CharSequence getPageTitle(int position){
        return list_title.get(position % list_title.size());

        //        //在title text前显示图片
//        Drawable dImage = context.getResources().getDrawable(tabImg[position]);
//        //left top right bottom
//        dImage.setBounds(0,0,dImage.getIntrinsicWidth(),dImage.getIntrinsicHeight());
//        //空格为了添加图片
//        SpannableString sp = new SpannableString("  "+list_title.get(position));
//        //对齐
//        ImageSpan imageSpan= new ImageSpan(dImage,ImageSpan.ALIGN_BOTTOM);
//        //跨度 what start end flags
//        sp.setSpan(imageSpan,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sp;
    }
}
