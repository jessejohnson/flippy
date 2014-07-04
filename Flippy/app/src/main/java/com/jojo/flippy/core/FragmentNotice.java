package com.jojo.flippy.core;

/**
 * Created by bright on 6/9/14.
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.jojo.flippy.profile.ImagePreviewActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FragmentNotice extends Fragment {

    ListView noticeList;
    NoticeListAdapter listAdapter;
    ArrayList<Notice> noticeFeed = new ArrayList<Notice>();
    private Intent intent;
    private String noticeTitle;
    private String noticeSubtitle;
    private String noticeId;
    private String noticeAvatar;
    private String noticeBody;
    private ProgressBar progressBarCommunityCenterLoader;

    public FragmentNotice() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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

                                try {
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                                    Date dateConverted = dateFormat.parse(timestampArray[0].toString());
                                    timestamp = formatter.format(dateConverted)+ " @ " + timestampArray[1].substring(0, 8);

                                } catch (Exception error) {
                                    //maintain the first format
                                }

                                String image_link = "";
                                if (!item.get("image_url").isJsonNull()) {
                                    image_link = item.get("image_url").getAsString();
                                }
                                noticeFeed.add(new Notice(item.get("id").getAsString(), author.get("first_name").getAsString(), author.get("last_name").getAsString(), item.get("title").getAsString(), "sub", item.get("content").getAsString(), timestamp, URI.create(image_link)));

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
            public void onItemClick(final AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                TextView noticeTitleTextView = (TextView) view.findViewById(R.id.textViewNoticeTitle);
                TextView textViewNoticeSubtitle = (TextView) view.findViewById(R.id.textViewNoticeSubtitle);
                TextView textViewNoticeText = (TextView) view.findViewById(R.id.textViewNoticeText);
                TextView textViewNoticeId = (TextView) view.findViewById(R.id.textViewNoticeId);
                noticeTitle = noticeTitleTextView.getText().toString().trim();
                noticeSubtitle = textViewNoticeSubtitle.getText().toString().trim();
                noticeBody = textViewNoticeText.getText().toString().trim();
                noticeId = textViewNoticeId.getText().toString().trim();

                intent = new Intent(getActivity(), NoticeDetailActivity.class);
                intent.putExtra("noticeTitle", noticeTitle);
                intent.putExtra("noticeSubtitle", noticeSubtitle);
                intent.putExtra("noticeBody", noticeBody);
                intent.putExtra("noticeId", noticeId);
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


}