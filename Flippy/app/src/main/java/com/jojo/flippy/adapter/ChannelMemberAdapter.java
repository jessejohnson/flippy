package com.jojo.flippy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jojo.flippy.app.R;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/13/14.
 */
public class ChannelMemberAdapter extends ArrayAdapter<ProfileItem> implements Filterable {
    Context context;
    boolean isManage;
    private List<ProfileItem> allModelItemsArray;
    private List<ProfileItem> filteredModelItemsArray;
    private ModelFilter filter;


    public ChannelMemberAdapter(Context context, int resourceId,
                                List<ProfileItem> items, boolean isManage) {
        super(context, resourceId, items);
        this.context = context;
        this.isManage = isManage;
        this.allModelItemsArray = new ArrayList<ProfileItem>();
        allModelItemsArray.addAll(items);
        this.filteredModelItemsArray = new ArrayList<ProfileItem>();
        filteredModelItemsArray.addAll(allModelItemsArray);
        getFilter();

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ModelFilter();
        }
        return filter;
    }


    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView textViewChannelMemberFirstName;
        TextView getTextViewChannelMemberSecondName;
        TextView textViewMemberId;
        LinearLayout linearLayoutChannelMember;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ProfileItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_members_listview, null);
            holder = new ViewHolder();
            holder.textViewChannelMemberFirstName = (TextView) convertView.findViewById(R.id.textViewMemberEmail);
            holder.textViewMemberId = (TextView) convertView.findViewById(R.id.textViewMemberId);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewMember);
            holder.getTextViewChannelMemberSecondName = (TextView) convertView.findViewById(R.id.textViewMemberFullName);
            holder.linearLayoutChannelMember = (LinearLayout) convertView.findViewById(R.id.linearLayoutChannelMember);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.textViewChannelMemberFirstName.setText(rowItem.getProfileChannelName());
        holder.textViewMemberId.setText(rowItem.getTextViewMemberId());
        holder.getTextViewChannelMemberSecondName.setText(rowItem.getProfileChannelTotalNumber());

        Ion.with(holder.imageView)
                .placeholder(R.drawable.user_place_small)
                .error(R.drawable.user_error_small)
                .animateIn(R.anim.fade_in)
                .load(String.valueOf(rowItem.getProfileChannelItem()));


        return convertView;
    }

    private class ModelFilter extends Filter {

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<ProfileItem> filteredItems = new ArrayList<ProfileItem>();
                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    Log.e("In the adapter query", allModelItemsArray.get(0).toString());
                    ProfileItem m = allModelItemsArray.get(i);
                    if (m.getProfileChannelName().toLowerCase().contains(constraint))
                        filteredItems.add(m);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = allModelItemsArray;
                    result.count = allModelItemsArray.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredModelItemsArray = (ArrayList<ProfileItem>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }


}

