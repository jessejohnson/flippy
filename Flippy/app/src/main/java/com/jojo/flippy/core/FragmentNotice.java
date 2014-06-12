package com.jojo.flippy.core;

/**
 * Created by bright on 6/9/14.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jojo.flippy.adapter.Notice;
import com.jojo.flippy.adapter.NoticeListAdapter;
import com.jojo.flippy.app.R;

import java.net.URI;
import java.util.ArrayList;

public class FragmentNotice extends Fragment {

    ListView noticeList;
    ListAdapter listAdapter;
    ArrayList<Notice> noticeFeed = new ArrayList<Notice>();


    public FragmentNotice() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notice, container,
                false);

        //populate noticeFeed with dummy values to test app
        noticeFeed.add(new Notice("1", "admin", "SRC2014", "Welcome Back!", "sub",
                "We the SRC2014 welcome all freshers to school, and all continuing students" +
                        " back to school", URI.create("http://www.citifmonline.com/wp-content/uploads/2014/05/UG-111.jpg")));
        noticeFeed.add(new Notice("2", "echo", "Echo Legon", "Fresher's Party!", "sub",
                "The semester begins with the hottest freshers at Aphro, Friday night!",
                URI.create("")));

        listAdapter = new NoticeListAdapter(this.getActivity(), noticeFeed);
        noticeList = (ListView) view.findViewById(R.id.listViewNoticeList);
        noticeList.setAdapter(listAdapter);

        //registering the notice list for context menu action
        registerForContextMenu(noticeList);


        return view;
    }

}