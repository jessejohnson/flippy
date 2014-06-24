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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jojo.flippy.app.R;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ChannelAdapter extends ArrayAdapter<Channel> {

    Context context;
    private  boolean isUserChannel;

    public ChannelAdapter(Context context, int resourceId,
                          List<Channel> items, boolean isUserChannels) {
        super(context, resourceId, items);
        this.context = context;
        this.isUserChannel=isUserChannels;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView,imageViewSubscribe;
        TextView textViewChannelName;
        TextView textViewNumberOfMembers;
        TextView textViewStatus;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Channel rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_listview, null);
            holder = new ViewHolder();
            holder.textViewNumberOfMembers = (TextView) convertView.findViewById(R.id.textViewChannelMembersCustom);
            holder.textViewChannelName = (TextView) convertView.findViewById(R.id.textViewChannelNameCustom);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewCommunityChannel);
            holder.textViewStatus =(TextView)convertView.findViewById(R.id.textViewChannelStatusCustom);
            holder.imageViewSubscribe = (ImageView)convertView.findViewById(R.id.imageViewSubscribe);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNumberOfMembers.setText(rowItem.getMembers());
        holder.textViewChannelName.setText(rowItem.getChannelName());
        Ion.with(holder.imageView)
                .placeholder(R.drawable.channel_placeholder)
                .error(R.drawable.channel_placeholder)
                .load(String.valueOf(rowItem.getImageUrl()));
        holder.textViewStatus.setText(rowItem.getStatus());

        //check to see if the adapter displays only user channel, then set the subscription button to invisible state
         if(isUserChannel){
             holder.imageViewSubscribe.setVisibility(convertView.GONE);
         }
        return convertView;
    }
}