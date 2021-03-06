package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentCommunities extends Fragment {
    public static String channelId;
    public static String channelName;
    List<Channel> rowItems;
    private ListView listViewCommunity;
    private Intent intent;
    private ProgressBar progressBarCommunityChannelLoader;
    private ChannelAdapter adapter;
    private TextView textViewEmptyCommunityChannel,textViewEmptyNoInternetCommunity;
    private ImageView imageViewNoInternetCommunities;

    public FragmentCommunities() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_communities, container,
                false);

        intent = new Intent();
        rowItems = new ArrayList<Channel>();
        progressBarCommunityChannelLoader = (ProgressBar) view.findViewById(R.id.progressBarCommunityChannelLoader);
        listViewCommunity = (ListView) view.findViewById(R.id.listViewCommunity);
        imageViewNoInternetCommunities = (ImageView)view.findViewById(R.id.imageViewNoInternetCommunities);
        textViewEmptyCommunityChannel = (TextView) view.findViewById(R.id.textViewEmptyCommunityChannel);
        textViewEmptyNoInternetCommunity = (TextView) view.findViewById(R.id.textViewEmptyNoInternetCommunity);
        textViewEmptyCommunityChannel.setVisibility(View.GONE);


        adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems, false);
        listViewCommunity.setAdapter(adapter);
        String url = Flippy.COMMUNITIES_URL + CommunityCenterActivity.userCommunityId + "/channels/";

        //Loading the list with data from Api call
        Ion.with(getActivity())
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarCommunityChannelLoader.setVisibility(view.GONE);
                        try {
                            if (result != null) {
                                JsonArray communityArray = result.getAsJsonArray("results");
                                for (int i = 0; i < communityArray.size(); i++) {
                                    JsonObject item = communityArray.get(i).getAsJsonObject();
                                    JsonObject creator = item.getAsJsonObject("creator");
                                    Channel channelItem = new Channel(URI.create(item.get("image_url").getAsString()), item.get("id").getAsString(), item.get("name").getAsString(), creator.get("email").getAsString(), creator.get("first_name").getAsString() + " " + creator.get("last_name").getAsString(),item.get("is_default").getAsBoolean());
                                    rowItems.add(channelItem);
                                }
                                updateListAdapter();
                            }
                            if (e != null) {
                                textViewEmptyNoInternetCommunity.setVisibility(View.VISIBLE);
                                imageViewNoInternetCommunities.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception exception) {
                            Log.e("Error community CHANNELS_URL", "error loading CHANNELS_URL in a community");
                        }
                    }
                });


        //Setting the click listener of the list view, if user clicks on a particular channel
        listViewCommunity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView textViewChannelId = (TextView) view.findViewById(R.id.textViewChannelId);
                TextView textViewChannelName = (TextView) view.findViewById(R.id.textViewChannelNameCustom);
                channelId = textViewChannelId.getText().toString();
                channelName = textViewChannelName.getText().toString();
                intent.setClass(getActivity(), ChannelDetailActivity.class);
                intent.putExtra("channelId", channelId);
                intent.putExtra("channelName", channelName);
                getActivity().startActivity(intent);


            }
        });

        return view;
    }

    private void updateListAdapter() {
        adapter.notifyDataSetChanged();
        if (adapter.isEmpty()) {
            textViewEmptyCommunityChannel.setVisibility(View.VISIBLE);
            textViewEmptyNoInternetCommunity.setVisibility(View.GONE);
        }
    }

}