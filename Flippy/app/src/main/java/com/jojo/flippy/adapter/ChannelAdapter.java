package com.jojo.flippy.adapter;

/**
 * Created by bright on 6/11/14.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ChannelAdapter extends ArrayAdapter<Channel> {

    Context context;
    private boolean isUserChannel;

    public ChannelAdapter(Context context, int resourceId,
                          List<Channel> items, boolean isUserChannels) {
        super(context, resourceId, items);
        this.context = context;
        this.isUserChannel = isUserChannels;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView, imageViewSubscribe;
        TextView textViewChannelName;
        TextView textViewNumberOfMembers;
        TextView textViewStatus;
        TextView textViewChannelId;

    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Channel rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_listview, null);
            holder = new ViewHolder();
            holder.textViewNumberOfMembers = (TextView) convertView.findViewById(R.id.textViewChannelMembersCustom);
            holder.textViewChannelName = (TextView) convertView.findViewById(R.id.textViewChannelNameCustom);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewCommunityChannel);
            holder.textViewStatus = (TextView) convertView.findViewById(R.id.textViewChannelStatusCustom);
            holder.imageViewSubscribe = (ImageView) convertView.findViewById(R.id.imageViewSubscribe);
            holder.textViewChannelId = (TextView) convertView.findViewById(R.id.textViewChannelId);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNumberOfMembers.setText(rowItem.getCreatorFullName());
        holder.textViewChannelName.setText(rowItem.getChannelName());
        Ion.with(holder.imageView)
                .placeholder(R.drawable.channel_bg)
                .animateIn(R.anim.fade_in)
                .error(R.color.flippy_orange)
                .load(String.valueOf(rowItem.getImageUrl()));
        holder.textViewStatus.setText(rowItem.getCreatorEmail());
        holder.textViewChannelId.setText(rowItem.getId());
        //check to see if the adapter displays only user channel, then set the subscription button to invisible state
        if (isUserChannel) {
            holder.imageViewSubscribe.setVisibility(convertView.GONE);
        }

        holder.imageViewSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeToChannel(rowItem.getId());
            }
        });
        return convertView;
    }

    private void subscribeToChannel(String id) {
        final String channelDetailSubscribeURL = Flippy.channelSubscribeURL + id + "/subscribe/";
        if (CommunityCenterActivity.userAuthToken == "") {
            ToastMessages.showToastLong(context, "Sorry, request cannot be made");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("id", CommunityCenterActivity.regUserID);
        Ion.with(context)
                .load(channelDetailSubscribeURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result != null) {
                            ToastMessages.showToastLong(context, result.get("detail").getAsString());
                            if (e != null) {
                                ToastMessages.showToastLong(context, context.getResources().getString(R.string.internet_connection_error_dialog_title));
                            }

                        }
                    }

                    ;
                });
    }
}