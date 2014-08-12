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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.util.Flippy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class FragmentChannel extends Fragment {
    private ListView ChannelListView;
    private List<Channel> rowItems;
    private ProgressBar progressBarChannelDataLoad;
    private Intent intent;
    private Button buttonAddChannel;
    private ChannelAdapter adapter;
    private TextView textViewEmptyChannel, textViewEmptyNoInternetChannel;
    private ImageView imageViewNoNetworkChannel;
    private Dao<Channels, Integer> channelDao;
    private List<Channels> channelList;
    private Channels channels;

    public FragmentChannel() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_channel, container,
                false);

        intent = new Intent();
        rowItems = new ArrayList<Channel>();
        ChannelListView = (ListView) view.findViewById(R.id.listViewChannels);
        textViewEmptyChannel = (TextView) view.findViewById(R.id.textViewEmptyChannel);
        textViewEmptyNoInternetChannel = (TextView) view.findViewById(R.id.textViewEmptyNoInternetChannel);
        textViewEmptyChannel.setVisibility(View.GONE);
        progressBarChannelDataLoad = (ProgressBar) view.findViewById(R.id.progressBarChannelDataLoad);
        imageViewNoNetworkChannel = (ImageView) view.findViewById(R.id.imageViewNoNetworkChannel);
        adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems, true);
        ChannelListView.setAdapter(adapter);
        //get the request url
        String url = Flippy.USERS_URL + CommunityCenterActivity.regUserID + "/subscriptions/";

        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            channelDao = databaseHelper.getChannelDao();
            channelList = channelDao.queryForAll();
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e("Fragment", "Error getting all user CHANNELS_URL");
        }


        //load the CHANNELS_URL user subscribed to
        Ion.with(getActivity())
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarChannelDataLoad.setVisibility(view.GONE);
                        try {
                            if (result != null) {
                                JsonArray communityArray = result.getAsJsonArray("results");
                                for (int i = 0; i < communityArray.size(); i++) {
                                    JsonObject item = communityArray.get(i).getAsJsonObject();
                                    JsonObject creator = item.getAsJsonObject("creator");
                                    String channel_id = item.get("id").getAsString();
                                    Channel channelItem = new Channel(URI.create(item.get("image_url").getAsString()), channel_id, item.get("name").getAsString(), creator.get("email").getAsString(), creator.get("first_name").getAsString() + " " + creator.get("last_name").getAsString(),item.get("is_default").getAsBoolean());

                                    if (channelList.isEmpty()) {
                                        channels = new Channels(channel_id);
                                        channelDao.createOrUpdate(channels);
                                    }
                                    rowItems.add(channelItem);
                                }
                                updateAdapter();

                            } else if (e != null) {
                                textViewEmptyNoInternetChannel.setVisibility(View.VISIBLE);
                                imageViewNoNetworkChannel.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("Fragment CHANNELS_URL", "something else went wrong");
                            }
                        } catch (Exception exception) {
                            Log.e("Fragment CHANNELS_URL", "Error loading CHANNELS_URL " + exception.toString());
                        }
                    }
                });

        //Setting the click listener of the list view, if user clicks on a particular channel
        ChannelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView textViewChannelId = (TextView) view.findViewById(R.id.textViewChannelId);
                TextView textViewChannelName = (TextView) view.findViewById(R.id.textViewChannelNameCustom);
                String channelId = textViewChannelId.getText().toString();
                String channelName = textViewChannelName.getText().toString();
                intent.setClass(getActivity(), ChannelDetailActivity.class);
                intent.putExtra("channelId", channelId);
                intent.putExtra("channelName", channelName);
                getActivity().startActivity(intent);

            }
        });


        buttonAddChannel = (Button) view.findViewById(R.id.buttonAddChannel);
        buttonAddChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(), CreateChannelActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        if (adapter.isEmpty()) {
            textViewEmptyChannel.setVisibility(View.VISIBLE);
            textViewEmptyNoInternetChannel.setVisibility(View.GONE);
        }
    }

}