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
import com.jojo.flippy.core.CommunityCenterActivity;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class AdminAdapter extends ArrayAdapter<AdminPerson> {
    Context context;

    public AdminAdapter(Context context, int resourceId,
                        List<AdminPerson> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        AdminPerson rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_admis_listview, null);
            holder = new ViewHolder();
            holder.imageViewAdminOne = (ImageView) convertView.findViewById(R.id.imageViewAdminOne);
            holder.imageViewDemoteUser = (ImageView) convertView.findViewById(R.id.imageViewDemoteUser);
            holder.textViewAdminEmail = (TextView) convertView.findViewById(R.id.textViewAdminEmail);
            holder.textViewAdminFullName = (TextView) convertView.findViewById(R.id.textViewAdminFullName);
            holder.textViewAdminId = (TextView) convertView.findViewById(R.id.textViewAdminId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textViewAdminEmail.setText(rowItem.getProfileEmail());
        holder.textViewAdminId.setText(rowItem.getAdminId());
        holder.textViewAdminFullName.setText(rowItem.getProfileFullName());

        Ion.with(holder.imageViewAdminOne)
                .placeholder(R.drawable.default_profile_picture)
                .error(R.drawable.default_profile_picture)
                .animateIn(R.anim.fade_in)
                .load(String.valueOf(rowItem.getAdminProfileItem()));

        return convertView;
    }

    private class ViewHolder {
        ImageView imageViewAdminOne,imageViewDemoteUser;
        TextView textViewAdminEmail,
                textViewAdminFullName,
                textViewAdminId;

    }
}