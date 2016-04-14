package myapplication.mynewsapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ttslso on 2016/3/15.
 */
public class HeadTitlePanel extends RelativeLayout {

    private Context mContext;
    private TextView mHeadTextView;
    private ImageView mHeadIcon;
    private ImageView mHeadDiviner;

    public HeadTitlePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View parent = LayoutInflater.from(mContext).
                inflate(R.layout.head_title_panel,this,true);
        mHeadTextView = (TextView)parent.findViewById(R.id.head_text_view);
/*      xml
        <myapplication.mynewsapp.HeadTitlePanel
        android:id="@+id/head_title_panel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true" />
        */
    }
}
