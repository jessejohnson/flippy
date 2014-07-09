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

public class CommunityAdapter extends ArrayAdapter<Community> {

    Context context;
    private ViewHolder holder;


    public CommunityAdapter(Context context, int resourceId,
                            List<Community> items) {
        super(context, resourceId, items);
        this.context = context;

    }

    private class ViewHolder {
        ImageView imageView;
        TextView textViewCommunityName;
        TextView textViewCommunityId;
        TextView textViewCommunityBio;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        final Community rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.select_community_listview, null);
            holder = new ViewHolder();
            holder.textViewCommunityId = (TextView) convertView.findViewById(R.id.textViewCommunityId);
            holder.textViewCommunityBio = (TextView) convertView.findViewById(R.id.textViewCommunityBio);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewCommunity);
            holder.textViewCommunityName = (TextView) convertView.findViewById(R.id.textViewCommunityName);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewCommunityId.setText(rowItem.getId());
        holder.textViewCommunityBio.setText(rowItem.getCommunityBio());
        Ion.with(holder.imageView)
                .placeholder(R.color.flippy_orange)
                .animateIn(R.anim.fade_in)
                .error(R.color.flippy_orange)
                .load(String.valueOf(rowItem.getImageUrl()));
        holder.textViewCommunityName.setText(rowItem.getCommunityName());

        return convertView;
    }
}