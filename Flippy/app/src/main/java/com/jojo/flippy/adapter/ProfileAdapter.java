package com.jojo.flippy.adapter;

/**
 * Created by bright on 6/12/14.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jojo.flippy.app.R;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<ProfileItem> {

    Context context;

    public ProfileAdapter(Context context, int resourceId,
                          List<ProfileItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView  imageViewChannelImage;
        TextView textViewProfileChannelName, textViewProfileChannelTotalMembers;
        LinearLayout linearLayoutProfile, linearLayoutProfileChannel;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ProfileItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.profile_listview, null);
            holder = new ViewHolder();
            holder.imageViewChannelImage = (ImageView) convertView.findViewById(R.id.imageViewChannelImage);
            holder.textViewProfileChannelName = (TextView) convertView.findViewById(R.id.textViewProfileChannelName);
            holder.textViewProfileChannelTotalMembers = (TextView) convertView.findViewById(R.id.textViewProfileChannelTotalMembers);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewProfileChannelName.setText(rowItem.getProfileChannelName());
        holder.textViewProfileChannelTotalMembers.setText(rowItem.getProfileChannelTotalNumber());

        Ion.with(holder.imageViewChannelImage)
                .load(String.valueOf(rowItem.getProfileChannelItem()));


        return convertView;
    }
}