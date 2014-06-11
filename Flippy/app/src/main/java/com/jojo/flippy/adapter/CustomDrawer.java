package com.jojo.flippy.adapter;

/**
 * Created by bright on 6/9/14.
 */
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jojo.flippy.app.R;

public class CustomDrawer extends ArrayAdapter<DrawerItem> {

    Context context;
    List<DrawerItem> drawerItemList;
    int layoutResID;

    public CustomDrawer(Context context, int layoutResourceID,
                               List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.ItemName = (TextView) view
                    .findViewById(R.id.drawer_itemName);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);

            drawerHolder.accountLayout = (LinearLayout) view
                    .findViewById(R.id.accountLayout);
            drawerHolder.itemLayout = (LinearLayout) view
                    .findViewById(R.id.itemLayout);

            drawerHolder.user_frame = (FrameLayout)view.findViewById(R.id.user_frame);

            drawerHolder.userImage = (ImageView) view.findViewById(R.id.user_pic);
            drawerHolder.userEmail = (TextView) view.findViewById(R.id.text_user_email);
            drawerHolder.userName = (TextView)view.findViewById(R.id.text_user_name);


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);
        if(position==0){
            //check if the drawer item is of index zero
            drawerHolder.itemLayout.setVisibility(View.GONE);
            drawerHolder.accountLayout.setVisibility(LinearLayout.VISIBLE);
            drawerHolder.userName.setText(R.string.dummy_user_name);
            drawerHolder.userEmail.setText(R.string.dummy_user_email);
            drawerHolder.userImage.setImageResource(R.drawable.sample_user);
        }else {
            //destroy all the other views if not in the item with the zeroth index
            drawerHolder.user_frame.setVisibility(View.GONE);
            drawerHolder.userImage.setVisibility(View.GONE);
            drawerHolder.accountLayout.setVisibility(View.GONE);
            drawerHolder.userName.setVisibility(View.GONE);
            drawerHolder.userEmail.setVisibility(view.GONE);
            //make visible the other layout and items
            drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);
            //Setting the parameters of the items in the view
            drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                    dItem.getImgResID()));
            drawerHolder.ItemName.setText(dItem.getItemName());
        }

        return view;
    }

    private static class DrawerItemHolder {
        TextView ItemName,userName,userEmail;
        ImageView icon;
        ImageView userImage;
        LinearLayout itemLayout,accountLayout;
        FrameLayout user_frame;
    }
}
