package com.jojo.flippy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.flippy.app.R;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by bright on 6/13/14.
 */
public class ChannelSelectionItemAdapter extends ArrayAdapter<ChannelSelectItem> {
    Context context;


    public ChannelSelectionItemAdapter(Context context, int resourceId,
                                       List<ChannelSelectItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageViewAChannelImage;
        TextView textViewChannelName;
        TextView textViewChannelDescription;
        TextView textViewChannelId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChannelSelectItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_select_listview, null);
            holder = new ViewHolder();
            holder.textViewChannelName = (TextView) convertView.findViewById(R.id.textViewChannelName);
            holder.textViewChannelId = (TextView) convertView.findViewById(R.id.textViewChannelId);
            holder.textViewChannelDescription = (TextView) convertView.findViewById(R.id.textViewChannelDescription);
            holder.imageViewAChannelImage = (ImageView) convertView.findViewById(R.id.imageViewAChannelImage);
            convertView.setTag(holder);
        } else
        holder = (ViewHolder) convertView.getTag();
        holder.textViewChannelId.setText(rowItem.getId());
        holder.textViewChannelName.setText(rowItem.getChannelName());
        holder.textViewChannelDescription.setText(rowItem.getChannelBio());
        Ion.with(holder.imageViewAChannelImage)
                .placeholder(R.drawable.channel_place)
                .error(R.drawable.channel_error)
                .load(String.valueOf(rowItem.getImageUrl()));


        return convertView;
    }


}
