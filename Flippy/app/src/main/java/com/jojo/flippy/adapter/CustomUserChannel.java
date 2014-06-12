package com.jojo.flippy.adapter;

/**
 * Created by bright on 6/11/14.
 */
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.flippy.app.R;

public class CustomUserChannel extends ArrayAdapter<ChannelItem> {

    Context context;

    public CustomUserChannel(Context context, int resourceId,
                                 List<ChannelItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView textViewChannelName;
        TextView textViewNumberOfMembers;
        TextView textViewStatus;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ChannelItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_channel_list_item, null);
            holder = new ViewHolder();
            holder.textViewNumberOfMembers = (TextView) convertView.findViewById(R.id.textViewNumberOfMembers);
            holder.textViewChannelName = (TextView) convertView.findViewById(R.id.textViewChannelName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewChannel);
            holder.textViewStatus =(TextView)convertView.findViewById(R.id.textViewMemberStatus);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNumberOfMembers.setText(rowItem.getMembers());
        holder.textViewChannelName.setText(rowItem.getChannelName());
        holder.imageView.setImageResource(rowItem.getImageId());
        holder.textViewStatus.setText(rowItem.getStatus());

        return convertView;
    }
}