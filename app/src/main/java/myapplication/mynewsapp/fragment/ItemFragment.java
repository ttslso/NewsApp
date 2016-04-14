package myapplication.mynewsapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import myapplication.mynewsapp.R;

/**
 * Created by ttslso on 2016/3/16.
 * 导航栏的Item
 */
public class ItemFragment extends Fragment {

    private TextView mTextView;
    private Bundle mBundle;
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,
                             Bundle saveInstanceState){
        View v = layoutInflater.inflate(R.layout.fragment_item, container, false);
        mTextView = (TextView) v.findViewById(R.id.text_view);

        mBundle = getArguments();
        String title = mBundle.getString("arg");

        mTextView.setText(title);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
    }
}
