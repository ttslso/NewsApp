package myapplication.mynewsapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myapplication.mynewsapp.R;

/**
 * Created by ttslso on 2016/4/8.
 */
public class ThirdFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.thrid_fragment,null);
        return view;
    }

    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
}
