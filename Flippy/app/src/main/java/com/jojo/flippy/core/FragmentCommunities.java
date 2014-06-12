package com.jojo.flippy.core;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jojo.flippy.adapter.ChannelItem;
import com.jojo.flippy.adapter.CustomChannel;
import com.jojo.flippy.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentCommunities extends Fragment {
    private ListView listViewCommunity;
    //Instance of the channel item
    List<ChannelItem> rowItems;


    public FragmentCommunities() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_communities, container,
                false);

        //Loading the list with a dummy data
        rowItems = new ArrayList<ChannelItem>();
        ChannelItem item = new ChannelItem(R.drawable.sample_user, "GESA KNUST", "200 members","active");
        ChannelItem item1 = new ChannelItem(R.drawable.user_profile, "SRC Legon, 2015", "4000 members","admin");
        rowItems.add(item);
        rowItems.add(item1);


        listViewCommunity = (ListView)view.findViewById(R.id.listViewCommunity);
        CustomChannel adapter = new CustomChannel(getActivity(),
                R.layout.channel_listview, rowItems,false);
        listViewCommunity.setAdapter(adapter);


        return view;
    }

}