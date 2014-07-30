package com.jojo.flippy.adapter;

/**
 * Created by bright on 6/12/14.
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

public class SettingsAdapter extends ArrayAdapter<SettingsItem> {

    Context context;

    public SettingsAdapter(Context context, int resourceId,
                           List<SettingsItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView textViewSettingsTitle;
        TextView textViewSettingsSubtitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SettingsItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.settings_list_item, null);
            holder = new ViewHolder();
            holder.textViewSettingsTitle = (TextView) convertView.findViewById(R.id.textViewSettingsTitle);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewSettingsIcon);
            holder.textViewSettingsSubtitle = (TextView) convertView.findViewById(R.id.textViewSettingsSubtitle);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.textViewSettingsTitle.setText(rowItem.getSettingTitle());
        holder.imageView.setImageResource(rowItem.getSettingIcon());
        holder.textViewSettingsSubtitle.setText(rowItem.getSettingSubTitle());

        return convertView;
    }
}