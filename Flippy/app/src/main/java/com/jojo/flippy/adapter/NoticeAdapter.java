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

public class NoticeAdapter extends ArrayAdapter<Notice> {

    Context context;
    private ViewHolder holder;


    public NoticeAdapter(Context context, int resourceId,
                         List<Notice> items) {
        super(context, resourceId, items);
        this.context = context;

    }


    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        final Notice rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.notice_list_item, null);
            holder = new ViewHolder();
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewNoticeTitle);
            holder.textViewSubtitle = (TextView) convertView.findViewById(R.id.textViewNoticeSubtitle);
            holder.imageViewNotice = (ImageView) convertView.findViewById(R.id.imageViewNoticeImage);
            holder.imageViewCreator = (ImageView) convertView.findViewById(R.id.imageViewNoticeCreator);
            holder.textViewContent = (TextView) convertView.findViewById(R.id.textViewNoticeText);
            holder.textViewId = (TextView) convertView.findViewById(R.id.textViewNoticeId);
            holder.textViewDateInfo = (TextView) convertView.findViewById(R.id.textViewNoticeDateInfo);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.textViewTitle.setText(rowItem.getTitle());
        holder.textViewSubtitle.setText(rowItem.getSubtitle());
        holder.textViewContent.setText(rowItem.getContent());
        holder.textViewDateInfo.setText(rowItem.getDateInfo());
        holder.textViewId.setText(rowItem.getId());

        Ion.with(holder.imageViewCreator)
                .placeholder(R.drawable.default_profile_picture)
                .error(R.drawable.default_profile_picture)
                .animateIn(R.anim.fade_in)
                .load(rowItem.getCreatorUrl().toString());

        String noticeImage = rowItem.getImageUrl().toString();

        if (noticeImage == null || noticeImage.equalsIgnoreCase("")) {
            holder.imageViewNotice.setVisibility(View.GONE);
        } else {
            Ion.with(holder.imageViewNotice)
                    .placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture)
                    .animateIn(R.anim.fade_in)
                    .load(noticeImage);
        }


        return convertView;
    }


    private class ViewHolder {
        ImageView imageViewNotice, imageViewCreator;
        TextView textViewTitle;
        TextView textViewSubtitle;
        TextView textViewContent;
        TextView textViewId;
        TextView textViewDateInfo;

    }


}