package com.jojo.flippy.adapter;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends ArrayAdapter<Channel> {

    Context context;
    String channelDetailSubscribeURL = Flippy.CHANNELS_URL;
    private boolean isUserChannel;
    private ViewHolder holder;
    private Dao<Channels, Integer> channelDao;
    private List<Channels> channelList;

    private static ArrayList<String> channelId = new ArrayList<String>();


    public ChannelAdapter(Context context, int resourceId,
                          List<Channel> items, boolean isUserChannels) {
        super(context, resourceId, items);
        this.context = context;
        this.isUserChannel = isUserChannels;

    }

    private void getUserSubscribedChannelId(Context context) {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class);
            channelDao = databaseHelper.getChannelDao();
            channelList = channelDao.queryForAll();
            channelId = new ArrayList<String>();
            if (!channelList.isEmpty()) {
                for (Channels channels : channelList) {
                    channelId.add(channels.channel_id);
                }
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e("Channel adapter", "Error getting all user channels");
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        final Channel rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        getUserSubscribedChannelId(context);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_listview, null);
            holder = new ViewHolder();
            holder.textViewNumberOfMembers = (TextView) convertView.findViewById(R.id.textViewChannelMembersCustom);
            holder.textViewChannelName = (TextView) convertView.findViewById(R.id.textViewChannelNameCustom);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewCommunityChannel);
            holder.textViewStatus = (TextView) convertView.findViewById(R.id.textViewChannelStatusCustom);
            holder.imageViewSubscribe = (ImageView) convertView.findViewById(R.id.imageViewSubscribe);
            holder.imageViewUnSubscribe = (ImageView) convertView.findViewById(R.id.imageViewUnSubscribe);
            holder.textViewChannelId = (TextView) convertView.findViewById(R.id.textViewChannelId);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNumberOfMembers.setText(rowItem.getCreatorFullName());
        holder.textViewChannelName.setText(rowItem.getChannelName());
        Ion.with(holder.imageView)
                .placeholder(R.drawable.channel_place)
                .animateIn(R.anim.fade_in)
                .error(R.drawable.channel_error)
                .load(String.valueOf(rowItem.getImageUrl()));
        holder.textViewStatus.setText(rowItem.getCreatorEmail());
        holder.textViewChannelId.setText(rowItem.getId());

        if (isUserChannel) {
            holder.imageViewSubscribe.setVisibility(convertView.GONE);
            holder.imageViewUnSubscribe.setVisibility(convertView.GONE);
        } else {
            //check the subscription states
            if (!channelId.isEmpty()) {
                if (channelId.contains(rowItem.getId())) {
                    holder.imageViewSubscribe.setVisibility(View.GONE);
                    holder.imageViewUnSubscribe.setVisibility(View.VISIBLE);
                } else {
                    holder.imageViewSubscribe.setVisibility(View.VISIBLE);
                    holder.imageViewUnSubscribe.setVisibility(View.GONE);
                }

            }
        }

        holder.imageViewSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSubscribe(rowItem.getId());
            }
        });
        holder.imageViewUnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUnSubscribe(rowItem.getId());
            }
        });
        return convertView;
    }

    private void setSubscribe(final String channelId) {
        if (CommunityCenterActivity.userAuthToken.equals("")) {
            ToastMessages.showToastLong(context, "Sorry, request cannot be made");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("id", CommunityCenterActivity.regUserID);
        Ion.with(context)
                .load(channelDetailSubscribeURL + channelId + "/subscribe/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("results")) {
                                    //add the channel id to the local channelTable
                                    Channels channels = new Channels(channelId);
                                    channelDao.createOrUpdate(channels);
                                    holder.imageViewUnSubscribe.setVisibility(View.VISIBLE);
                                    holder.imageViewSubscribe.setVisibility(View.GONE);
                                    ToastMessages.showToastLong(context, result.get("results").getAsString());
                                }
                                if (result.has("detail")) {
                                    ToastMessages.showToastLong(context, result.get("detail").getAsString());
                                }

                            }
                            if (e != null) {
                                Log.e("Channel adapter", e.toString());
                            }
                        } catch (Exception exception) {
                            Log.e("Channel Adapter", "Error subscribing to a channel " + channelId);
                        }

                    }
                });
    }

    private void setUnSubscribe(final String channelId) {
        if (CommunityCenterActivity.userAuthToken.equals("")) {
            ToastMessages.showToastLong(context, "Sorry, request cannot be made");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("id", CommunityCenterActivity.regUserID);
        Ion.with(context)
                .load(channelDetailSubscribeURL + channelId + "/unsubscribe/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("results")) {
                                    //removing the channel id
                                    channelDao.deleteById(Integer.parseInt(channelId));
                                    holder.imageViewUnSubscribe.setVisibility(View.GONE);
                                    holder.imageViewSubscribe.setVisibility(View.VISIBLE);
                                    ToastMessages.showToastLong(context, result.get("results").getAsString());
                                }
                                if (result.has("detail")) {
                                    ToastMessages.showToastLong(context, result.get("detail").getAsString());
                                }
                            }
                            if (e != null) {
                                ToastMessages.showToastLong(context, context.getResources().getString(R.string.internet_connection_error_dialog_title));
                            }
                        } catch (Exception exception) {
                            Log.e("Channel adapter", "Error un-subscribing to the channel " + channelId);
                        }
                    }
                });
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView, imageViewSubscribe, imageViewUnSubscribe;
        TextView textViewChannelName;
        TextView textViewNumberOfMembers;
        TextView textViewStatus;
        TextView textViewChannelId;

    }


}