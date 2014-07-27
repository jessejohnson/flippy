package com.jojo.flippy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.profile.ImagePreviewActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class NoticeListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Notice> noticeArrayList = new ArrayList<Notice>();

    public NoticeListAdapter(Activity a, ArrayList<Notice> list) {
        this.context = a;
        this.noticeArrayList = list;
    }

    @Override
    public int getCount() {
        return noticeArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return noticeArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {

        return Long.valueOf(noticeArrayList.get(i).getId());
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.notice_list_item, viewGroup, false);
        TextView title = (TextView) v.findViewById(R.id.textViewNoticeTitle);
        TextView subtitle = (TextView) v.findViewById(R.id.textViewNoticeSubtitle);
        ImageView image = (ImageView) v.findViewById(R.id.imageViewNoticeImage);
        image.setVisibility(View.GONE);
        TextView content = (TextView) v.findViewById(R.id.textViewNoticeText);
        TextView id = (TextView) v.findViewById(R.id.textViewNoticeId);
        TextView textViewNoticeDateInfo = (TextView) v.findViewById(R.id.textViewNoticeDateInfo);


        title.setText(noticeArrayList.get(i).getTitle());
        id.setText(noticeArrayList.get(i).getId());
        textViewNoticeDateInfo.setText(noticeArrayList.get(i).getDateInfo());
        subtitle.setText(noticeArrayList.get(i).getSubtitle());
        content.setText(noticeArrayList.get(i).getContent());


        final ImageView creatorImage = (ImageView) v.findViewById(R.id.imageViewNoticeCreator);
        Ion.with(creatorImage)
                .error(R.drawable.default_profile_picture)
                .placeholder(R.drawable.default_profile_picture)
                .load(noticeArrayList.get(i).getCreatorUrl().toString());
        //if a notice came without an image or with image
        if (String.valueOf(noticeArrayList.get(i).getImageUrl()).equalsIgnoreCase("flip")) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            Ion.with(image)
                    .animateIn(R.anim.fade_in)
                    .load(String.valueOf(noticeArrayList.get(i).getImageUrl()));
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(context, ImagePreviewActivity.class);
                intent.putExtra("avatar", noticeArrayList.get(i).getImageUrl().toString());
                intent.putExtra("imageName",noticeArrayList.get(i).getTitle());
                context.startActivity(intent);
            }
        });

        return v;
    }

    private void rateNotice(String Id) {
        String ratingURL = Flippy.allPostURL + Id + "/star/";
        JsonObject json = new JsonObject();
        json.addProperty("id", Id);
        Ion.with(context)
                .load(ratingURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            ToastMessages.showToastLong(context, result.get("results").getAsString());
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(context, context.getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }

                });

    }

}
