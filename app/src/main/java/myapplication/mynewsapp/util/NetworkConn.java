package myapplication.mynewsapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ttslso on 2016/5/8.
 */
public class NetworkConn {

    public static boolean isConnected(Context context){
        if(context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                return info.isAvailable();
            }
        }
        return false;
    }
}
