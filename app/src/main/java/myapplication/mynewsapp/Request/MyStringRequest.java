package myapplication.mynewsapp.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by ttslso on 2016/4/30.
 */
public class MyStringRequest extends StringRequest {

    public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super( url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String str = null;
        try {
            str = new String(response.data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Response.success(str,
                HttpHeaderParser.parseCacheHeaders(response));
    }
}
