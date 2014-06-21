package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentCommunities extends Fragment {
    private ListView listViewCommunity;
    //Instance of the channel item

    private Intent intent;
    private String channelsURL= "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    private TextView textViewCommunityEmpty;
    private ProgressBar progressBarCommunityChannelLoader;



    public FragmentCommunities() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_communities, container,
                false);

        intent = new Intent();
        textViewCommunityEmpty =(TextView)view.findViewById(R.id.textViewCommunityEmpty);
        progressBarCommunityChannelLoader = (ProgressBar)view.findViewById(R.id.progressBarCommunityChannelLoader);
        listViewCommunity = (ListView) view.findViewById(R.id.listViewCommunity);
        listViewCommunity.setEmptyView(textViewCommunityEmpty);
        final List<Channel> rowItems = new ArrayList<Channel>();
        //Loading the list with a dummy data
        Ion.with(getActivity())
                .load(channelsURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarCommunityChannelLoader.setVisibility(view.GONE);
                        if (result != null) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                Channel channelItem = new Channel(URI.create(item.get("image_url").getAsString()), item.get("name").getAsString(),"200 members", "active");
                                rowItems.add(channelItem);
                            }

                          Log.e("List community",rowItems.toString());

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(getActivity(), "Check internet connection");
                            Log.e("error", e.toString());
                        }

                    }
                });

        ChannelAdapter adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems, false);
        listViewCommunity.setAdapter(adapter);



        //registering the list view for context menu actions
        registerForContextMenu(listViewCommunity);
        return view;
    }

}