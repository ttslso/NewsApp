package myapplication.mynewsapp.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by ttslso on 2016/3/18.
 */
public class HttpUtils {

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static void get(String url, ResponseHandlerInterface responseHandler) {
        client.get(Constant.BASEURL + url, responseHandler);
    }

    public static void getImage(String url, ResponseHandlerInterface responseHandler) {
        client.get(url, responseHandler);
    }
}


