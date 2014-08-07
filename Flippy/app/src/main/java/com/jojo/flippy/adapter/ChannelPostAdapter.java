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

public class ChannelPostAdapter extends ArrayAdapter<ProfileItem> {
    Context context;

    public ChannelPostAdapter(Context context, int resourceId,
                              List<ProfileItem> items) {
        super(context, resourceId, items);
        this.context = context;


    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageViewChannelNoticeImage;
        TextView textViewNoticeChannelTitle;
        TextView textViewChannelNoticeDetail;
        TextView textViewChannelNoticeId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ProfileItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_post_listview, null);
            holder = new ViewHolder();
            holder.textViewNoticeChannelTitle = (TextView) convertView.findViewById(R.id.textViewChannelPostTitle);
            holder.textViewChannelNoticeId = (TextView) convertView.findViewById(R.id.textViewChannelNoticeId);
            holder.imageViewChannelNoticeImage = (ImageView) convertView.findViewById(R.id.imageViewChannelNoticeImage);
            holder.textViewChannelNoticeDetail = (TextView) convertView.findViewById(R.id.textViewChannelNoticeDetail);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewNoticeChannelTitle.setText(rowItem.getProfileChannelName());
        holder.textViewChannelNoticeDetail.setText(rowItem.getProfileChannelTotalNumber());
        holder.textViewChannelNoticeId.setText(rowItem.getTextViewMemberId());
        Ion.with(holder.imageViewChannelNoticeImage)
                .placeholder(R.drawable.notice_place)
                .error(R.drawable.notice_error)
                .animateIn(R.anim.fade_in)
                .load(String.valueOf(rowItem.getProfileChannelItem()));

        return convertView;
    }


}
