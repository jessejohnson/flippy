package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
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
    List<Channel> rowItems;
    private Intent intent;
    private TextView textViewCommunityEmpty;
    private ProgressBar progressBarCommunityChannelLoader;
    private ChannelAdapter adapter;



    public FragmentCommunities() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_communities, container,
                false);

        intent = new Intent();
        rowItems = new ArrayList<Channel>();
        textViewCommunityEmpty =(TextView)view.findViewById(R.id.textViewCommunityEmpty);
        progressBarCommunityChannelLoader = (ProgressBar)view.findViewById(R.id.progressBarCommunityChannelLoader);
        listViewCommunity = (ListView) view.findViewById(R.id.listViewCommunity);
        listViewCommunity.setEmptyView(textViewCommunityEmpty);
        textViewCommunityEmpty.setVisibility(view.GONE);

        adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems, false);
        listViewCommunity.setAdapter(adapter);
        //getting the user community url
        String url = Flippy.channelsInCommunityURL + CommunityCenterActivity.userCommunityId+"/channels/";
        //Loading the list with data from Api call
        Ion.with(getActivity())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        textViewCommunityEmpty.setVisibility(View.VISIBLE);
                        progressBarCommunityChannelLoader.setVisibility(view.GONE);
                        if (result != null) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                Channel channelItem = new Channel(URI.create(item.get("image_url").getAsString()),item.get("id").getAsString(), item.get("name").getAsString(),"200 members", "active");
                                rowItems.add(channelItem);
                            }
                            updateListAdapter();

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(getActivity(),getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });

        //registering the list view for context menu actions
        registerForContextMenu(listViewCommunity);
        return view;
    }

    private void updateListAdapter() {
        adapter.notifyDataSetChanged();
    }

}