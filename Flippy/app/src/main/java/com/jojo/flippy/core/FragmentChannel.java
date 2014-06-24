package com.jojo.flippy.core;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.ManageChannelActivity;
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

public class FragmentChannel extends Fragment {
    //Instance of the user channel
    ListView ChannelListView;
    //Instance of the channel item
    List<Channel> rowItems;
    private TextView textViewChannelNoData;
    private ProgressBar progressBarChannelDataLoad;

    private Intent intent;
    private String channelName = "SRC channel";
    private String totalMembers = "125";
    private Button buttonAddChannel;
    private String isManageActivity = "false";
    private String userChannels = "/subscriptions/";
    private ChannelAdapter adapter;


    public FragmentChannel() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_channel, container,
                false);

        intent = new Intent();
        //Loading the list with a dummy data
        rowItems = new ArrayList<Channel>();
        ChannelListView = (ListView) view.findViewById(R.id.listViewChannels);
        progressBarChannelDataLoad = (ProgressBar) view.findViewById(R.id.progressBarChannelDataLoad);
        textViewChannelNoData = (TextView) view.findViewById(R.id.textViewChannelNoData);
        ChannelListView.setEmptyView(textViewChannelNoData);
        adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems, true);
        ChannelListView.setAdapter(adapter);
        //get the request url
        String url = Flippy.userChannelsSubscribedURL + CommunityCenterActivity.regUserID + userChannels;

        //load the channels user subscribed to
        Ion.with(getActivity())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        textViewChannelNoData.setVisibility(View.VISIBLE);
                        progressBarChannelDataLoad.setVisibility(view.GONE);
                        if (result != null) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                Channel channelItem = new Channel(URI.create(item.get("image_url").getAsString()),item.get("id").getAsString(), item.get("name").getAsString(), "200 members", "active");
                                rowItems.add(channelItem);
                            }
                            updateAdapter();

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(getActivity(), getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });

        //Setting the click listener of the list view, if user clicks on a particular channel
        ChannelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView textViewChannelId = (TextView)view.findViewById(R.id.textViewChannelId);
                String channelId = textViewChannelId.getText().toString();
                intent.putExtra("channelId",channelId);
                //setting the click action for each of the items
                intent.setClass(getActivity(), ChannelMembers.class);
                intent.putExtra("channelName", channelName);
                intent.putExtra("totalMembers", totalMembers);
                intent.putExtra("isManageActivity", isManageActivity);
                startActivity(intent);


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


        registerForContextMenu(ChannelListView);
        return view;
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

}