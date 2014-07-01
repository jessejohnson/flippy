package com.jojo.flippy.core;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jojo.flippy.app.R;

import java.util.ArrayList;

/**
 * Created by odette on 7/1/14.
 */
public class HelpTextFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private static ArrayList<Integer> HelpStringIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_help_text, container, false);

        HelpStringIds = new ArrayList<Integer>();
        HelpStringIds.add(R.string.help_string_one);
        HelpStringIds.add(R.string.help_string_two);
        HelpStringIds.add(R.string.help_string_three);
        HelpStringIds.add(R.string.help_string_four);

        TextView helpText = (TextView) rootView.findViewById(R.id.textViewHelpText);
        helpText.setText(getString(HelpStringIds.get(mPageNumber)));
        return rootView;
    }

    public static HelpTextFragment create(int pageNumber){
        HelpTextFragment fragment = new HelpTextFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }
}
