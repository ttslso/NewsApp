package myapplication.mynewsapp.model;

import android.graphics.drawable.Drawable;

/**
 * Created by ttslso on 2016/4/30.
 */
public class MenuItem {
    private String mTitle;
    private Drawable mDrawable;

    public MenuItem(String mTitle, Drawable mDrawable) {
        this.mTitle = mTitle;
        this.mDrawable = mDrawable;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }


}