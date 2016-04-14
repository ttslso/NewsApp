package myapplication.mynewsapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import myapplication.mynewsapp.fragment.SecondFragment;
import myapplication.mynewsapp.fragment.ThirdFragment;
import myapplication.mynewsapp.fragment.FirstFragment;

/**
 * Created by ttslso on 2016/4/8.
 */
public class RadioFragmentAdapter extends FragmentStatePagerAdapter {
    static final int NUM_ITEMS = 3;

    public RadioFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null ;
        switch (i){
            case 0 :
                fragment = new FirstFragment();
                break;
            case 1 :
                fragment = new SecondFragment();
                break;
            case 2 :
                fragment = new ThirdFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
