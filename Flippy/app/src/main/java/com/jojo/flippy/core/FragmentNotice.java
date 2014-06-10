package com.jojo.flippy.core;

/**
 * Created by bright on 6/9/14.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.flippy.app.R;

public class FragmentNotice extends Fragment {

    ImageView ivIcon;
    TextView tvItemName;


    public FragmentNotice() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notice, container,
                false);

        ivIcon = (ImageView) view.findViewById(R.id.frag1_icon);
        tvItemName = (TextView) view.findViewById(R.id.frag1_text);


        return view;
    }

}