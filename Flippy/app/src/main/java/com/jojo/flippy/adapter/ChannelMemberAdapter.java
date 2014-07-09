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
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by bright on 6/13/14.
 */
public class ChannelMemberAdapter extends ArrayAdapter<ProfileItem> {
    Context context;
    boolean isManage;

    public ChannelMemberAdapter(Context context, int resourceId,
                                List<ProfileItem> items,boolean isManage) {
        super(context, resourceId, items);
        this.context = context;
        this.isManage = isManage;

    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView,imageViewMemberAdded;
        TextView textViewChannelMemberFirstName;
        TextView getTextViewChannelMemberSecondName;
        TextView textViewMemberId;
        LinearLayout linearLayoutChannelMember;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ProfileItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_members_listview, null);
            holder = new ViewHolder();
            holder.textViewChannelMemberFirstName = (TextView) convertView.findViewById(R.id.textViewMemberEmail);
            holder.textViewMemberId = (TextView) convertView.findViewById(R.id.textViewMemberId);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewMember);
            holder.imageViewMemberAdded = (ImageView) convertView.findViewById(R.id.imageViewMemberAdded);
            holder.getTextViewChannelMemberSecondName = (TextView) convertView.findViewById(R.id.textViewMemberFullName);
            holder.linearLayoutChannelMember = (LinearLayout) convertView.findViewById(R.id.linearLayoutChannelMember);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (position % 2 ==0) {
          holder.linearLayoutChannelMember.setBackgroundResource(R.drawable.flippy_background_light_dark);
        }
        if(!isManage){
           holder.imageViewMemberAdded.setVisibility(View.GONE);
        }
        holder.textViewChannelMemberFirstName.setText(rowItem.getProfileChannelName());
        holder.textViewMemberId.setText(rowItem.getTextViewMemberId());
        holder.getTextViewChannelMemberSecondName.setText(rowItem.getProfileChannelTotalNumber());

        Ion.with(holder.imageView)
                .placeholder(R.drawable.default_profile_picture)
                .error(R.drawable.default_profile_picture)
                .animateIn(R.anim.fade_in)
                .load(String.valueOf(rowItem.getProfileChannelItem()));


        return convertView;
    }


}
