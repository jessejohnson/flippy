package com.jojo.flippy.core;

/**
 * Created by bright on 6/9/14.
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.Notice;
import com.jojo.flippy.adapter.NoticeListAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;

public class FragmentNotice extends Fragment {

    ListView noticeList;
    NoticeListAdapter listAdapter;
    ArrayList<Notice> noticeFeed = new ArrayList<Notice>();
    private Intent intent;
    private String noticeTitle;
    private String noticeSubtitle;
    private String noticeID;
    private String noticeBody;
    private String noticeAvatar;
    private boolean isAttachedWithImage = true;
    private ProgressBar progressBarCommunityCenterLoader;
    private String channelName;


    public FragmentNotice() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_notice, container,
                false);


        listAdapter = new NoticeListAdapter(this.getActivity(), noticeFeed);
        noticeList = (ListView) view.findViewById(R.id.listViewNoticeList);
        progressBarCommunityCenterLoader = (ProgressBar) view.findViewById(R.id.progressBarLoadNoticeData);
        noticeList.setAdapter(listAdapter);


        String url = Flippy.allPostURL;
        //Loading the list with data from Api call
        Ion.with(getActivity())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarCommunityCenterLoader.setVisibility(view.GONE);
                        if (result != null) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                JsonObject author = item.getAsJsonObject("author");
                                String[] timestampArray = item.get("timestamp").getAsString().replace("Z", "").split("T");
                                String timestamp = timestampArray[0].toString() + " @ " + timestampArray[1].substring(0, 8);
                                String image_link = "";
                                if (!item.get("image_url").isJsonNull()) {
                                    image_link = item.get("image_url").getAsString();
                                }
                                noticeFeed.add(new Notice(item.get("id").getAsString(), author.get("first_name").getAsString(), channelName, item.get("title").getAsString(), "sub", item.get("content").getAsString(), timestamp, URI.create(image_link)));

                            }
                            updateListAdapter();

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(getActivity(), getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });


        //Setting the click listener for the notice list
        noticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                ImageView imageViewNoticeImage = (ImageView) view.findViewById(R.id.imageViewNoticeImage);
                TextView noticeTitleTextView = (TextView) view.findViewById(R.id.textViewNoticeTitle);
                TextView textViewNoticeSubtitle = (TextView) view.findViewById(R.id.textViewNoticeSubtitle);
                TextView textViewNoticeText = (TextView) view.findViewById(R.id.textViewNoticeText);
                TextView textViewNoticeId = (TextView) view.findViewById(R.id.textViewNoticeId);
                noticeTitle = noticeTitleTextView.getText().toString().trim();
                noticeSubtitle = textViewNoticeSubtitle.getText().toString().trim();
                noticeBody = textViewNoticeText.getText().toString().trim();
                noticeID = textViewNoticeId.getText().toString().trim();
                intent = new Intent(getActivity(), NoticeDetailActivity.class);
                intent.putExtra("noticeTitle", noticeTitle);
                intent.putExtra("noticeSubtitle", noticeSubtitle);
                intent.putExtra("noticeBody", noticeBody);
                if (imageViewNoticeImage.getVisibility() == view.GONE) {
                    isAttachedWithImage = false;
                }
                intent.putExtra("isAttachedWithImage", isAttachedWithImage);
                startActivity(intent);

            }
        });

        //registering the list view for context menu actions
        registerForContextMenu(noticeList);


        return view;
    }


    private void updateListAdapter() {
        listAdapter.notifyDataSetChanged();
    }

    private void getChannelName(String id) {
        //load the channel name using the channel id
        Ion.with(getActivity())
                .load(Flippy.channelDetailURL + id + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception ex, JsonObject nameResult) {

                        if (nameResult != null) {
                            channelName = nameResult.get("name").getAsString();
                        }
                        if (ex != null) {
                            ToastMessages.showToastLong(getActivity(), getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

}