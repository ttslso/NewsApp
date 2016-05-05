package myapplication.mynewsapp.model;

import com.google.gson.Gson;

/**
 * Created by ttslso on 2016/5/5.
 */
public class StartImage {

    /**
     * text : Greg Rakozy
     * img : https://pic1.zhimg.com/401173973410e9291060c8166432233c.jpg
     */

    private String text;
    private String img;

    public static StartImage objectFromData(String str) {

        return new Gson().fromJson(str, StartImage.class);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
