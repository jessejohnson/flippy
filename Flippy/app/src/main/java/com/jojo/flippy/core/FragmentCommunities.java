package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentCommunities extends Fragment {
    private ListView listViewCommunity;
    //Instance of the channel item
    List<Channel> rowItems;
    private Intent intent;
    private String channelName = "SRC channel";
    private String totalMembers = "125";
    private String isManageActivity = "false";


    public FragmentCommunities() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_communities, container,
                false);

        intent = new Intent();

        //Loading the list with a dummy data
        rowItems = new ArrayList<Channel>();
        Channel item = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"), "GESA KNUST", "200 members", "active");
        Channel item1 = new Channel(URI.create("http://www.ugsrc.com/wp-content/uploads/2013/08/6.jpg"), "SRC Legon, 2015", "4000 members", "admin");
        rowItems.add(item);
        rowItems.add(item1);


        listViewCommunity = (ListView) view.findViewById(R.id.listViewCommunity);
        ChannelAdapter adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems, false);
        listViewCommunity.setAdapter(adapter);


        //registering the list view for context menu actions
        registerForContextMenu(listViewCommunity);


        return view;
    }

}