package com.jojo.flippy.core;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentChannel extends Fragment {
    //Instance of the user channel
    ListView ChannelListView;
    //Instance of the channel item
    List<Channel> rowItems;


    public FragmentChannel() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_channel, container,
                false);


        //Loading the list with a dummy data
        rowItems = new ArrayList<Channel>();
        Channel item = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "GESA KNUST", "200 members","active");
        Channel item1 = new Channel(URI.create("http://www.ugsrc.com/wp-content/uploads/2013/08/6.jpg"), "SRC Legon, 2015", "4000 members","admin");
        rowItems.add(item);
        rowItems.add(item1);


        ChannelListView = (ListView)view.findViewById(R.id.listViewChannels);
        ChannelAdapter adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems,true);
        ChannelListView.setAdapter(adapter);

        return view;
    }

}