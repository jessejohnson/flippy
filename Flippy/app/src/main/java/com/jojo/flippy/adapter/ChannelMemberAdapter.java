package com.jojo.flippy.adapter;

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

import java.util.List;

/**
 * Created by bright on 6/13/14.
 */
public class ChannelMemberAdapter extends ArrayAdapter<SettingsItem> {
    Context context;

    public ChannelMemberAdapter(Context context, int resourceId,
                                List<SettingsItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView textViewChannelMemberFirstName;
        TextView getTextViewChannelMemberSecondName;
        LinearLayout linearLayoutChannelMember;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SettingsItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_members_listview, null);
            holder = new ViewHolder();
            holder.textViewChannelMemberFirstName = (TextView) convertView.findViewById(R.id.textViewMemberFirstName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewMember);
            holder.getTextViewChannelMemberSecondName = (TextView) convertView.findViewById(R.id.textViewMemberLastName);
            holder.linearLayoutChannelMember = (LinearLayout) convertView.findViewById(R.id.linearLayoutChannelMember);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (position % 2 ==0) {
          holder.linearLayoutChannelMember.setBackgroundResource(R.drawable.flippy_background_light_dark);
        }
        holder.textViewChannelMemberFirstName.setText(rowItem.getSettingTitle());
        holder.imageView.setImageResource(rowItem.getSettingIcon());
        holder.getTextViewChannelMemberSecondName.setText(rowItem.getSettingSubTitle());

        return convertView;
    }

}
