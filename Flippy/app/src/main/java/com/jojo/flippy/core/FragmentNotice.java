package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.Notice;
import com.jojo.flippy.adapter.NoticeListAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.InternetConnectionDetector;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNotice extends Fragment {

    public static IntentFilter postIntentFilter;
    ListView noticeList;
    NoticeListAdapter listAdapter;
    ArrayList<Notice> noticeFeed = new ArrayList<Notice>();
    BroadcastReceiver postReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAdapterFromDatabaseOnReceive();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };
    private Intent intent;
    private String noticeTitle;
    private String noticeSubtitle;
    private String noticeId;
    private String noticeBody;
    private ProgressBar progressBarCommunityCenterLoader;
    private Dao<Post, Integer> postDao;
    private InternetConnectionDetector internetConnectionDetector;
    private View view;
    private TextView textViewNoNotice;

    public FragmentNotice() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notice, container,
                false);


        listAdapter = new NoticeListAdapter(this.getActivity(), noticeFeed);
        noticeList = (ListView) view.findViewById(R.id.listViewNoticeList);
        textViewNoNotice = (TextView) view.findViewById(R.id.textViewNoNotice);
        textViewNoNotice.setVisibility(View.GONE);
        progressBarCommunityCenterLoader = (ProgressBar) view.findViewById(R.id.progressBarLoadNoticeData);
        noticeList.setAdapter(listAdapter);


        internetConnectionDetector = new InternetConnectionDetector(getActivity());
        if (internetConnectionDetector.isConnectingToInternet()) {
            postIntentFilter = new IntentFilter();
            postIntentFilter.addAction("newPostArrived");
            getActivity().registerReceiver(postReceiver, postIntentFilter);

        } else {
            Crouton.makeText(getActivity(), "You are currently offline", Style.ALERT)
                    .show();
        }

        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (postList.isEmpty()) {
                getAllPost(view);
            } else {
                loadAdapterFromDatabase(view);
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Crouton.makeText(getActivity(), "Sorry, Try again later", Style.ALERT)
                    .show();

        }

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

        registerForContextMenu(noticeList);
        return view;
    }

    private void updateListAdapter() {
        listAdapter.notifyDataSetChanged();
        if(listAdapter.isEmpty()){
            textViewNoNotice.setVisibility(View.VISIBLE);
            textViewNoNotice.setText("Currently no notice");
        }
    }


    private void persistPost(
            String notice_id, String notice_title, String notice_body, String notice_image, String start_date, String author_email, String author_id, String author_first_name, String author_last_name, String channel_id) {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            Calendar calendar = Calendar.getInstance();
            Post post = new Post(notice_id, notice_title, notice_body, notice_image, start_date, author_email, author_id, author_first_name, author_last_name, channel_id,calendar.getTimeInMillis()+"");
            postDao.create(post);
            loadAdapterFromDatabase(view);

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            ToastMessages.showToastLong(getActivity(), "Sorry, Failed to save post");

        }

    }

    private void getAllPost(final View view) {
        String url = Flippy.allPostURL;
        Ion.with(getActivity())
                .load(url)
                .setTimeout(60 * 60 * 1000)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarCommunityCenterLoader.setVisibility(view.GONE);
                        if (result != null && result.has("results")) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                JsonObject author = item.getAsJsonObject("author");
                                String startDate = item.get("timestamp").getAsString();
                                String title = item.get("title").getAsString();
                                String id = item.get("id").getAsString();
                                String content = item.get("content").getAsString();
                                String channel = item.get("channel").getAsString();
                                String image_link = "flip";
                                if (!item.get("image_url").isJsonNull()) {
                                    image_link = item.get("image_url").getAsString();
                                }
                                String authorEmail = author.get("email").getAsString();
                                String authorId = author.get("id").getAsString();
                                String authorFirstName = author.get("first_name").getAsString();
                                String authorLastName = author.get("last_name").getAsString();
                                persistPost(id, title, content, image_link, startDate, authorEmail, authorId, authorFirstName, authorLastName, channel);

                            }

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(getActivity(), getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });

    }

    private void loadAdapterFromDatabase(View view) {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            progressBarCommunityCenterLoader.setVisibility(view.GONE);
            if (!postList.isEmpty()) {
                for (int i = 0; i < postList.size(); i++) {
                    Post post = postList.get(i);
                    String[] timestampArray = post.start_date.replace("Z", "").split("T");
                    String timestamp = timestampArray[0].toString() + " @ " + timestampArray[1].substring(0, 8);

                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                        Date dateConverted = dateFormat.parse(timestampArray[0].toString());
                        timestamp = formatter.format(dateConverted) + " @ " + timestampArray[1].substring(0, 8);
                    } catch (Exception error) {
                        //maintain the first format
                    }
                    String subtitle = post.author_first_name+", " +post.author_first_name;
                    noticeFeed.add(new Notice(post.notice_id, post.notice_title,subtitle, post.notice_body,post.author_id,post.channel_id, timestamp, URI.create(post.notice_image)));

                }
                updateListAdapter();

            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Crouton.makeText(getActivity(), "Sorry, Try again later", Style.ALERT)
                    .show();

        }

    }

    private void loadAdapterFromDatabaseOnReceive() {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (!postList.isEmpty()) {
                noticeFeed.clear();
                for (int i = 0; i < postList.size(); i++) {
                    Post post = postList.get(i);
                    String[] timestampArray = post.start_date.replace("Z", "").split("T");
                    String timestamp = timestampArray[0].toString() + " @ " + timestampArray[1].substring(0, 8);

                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                        Date dateConverted = dateFormat.parse(timestampArray[0].toString());
                        timestamp = formatter.format(dateConverted) + " @ " + timestampArray[1].substring(0, 8);
                    } catch (Exception error) {
                        //maintain the first format
                    }
                    String subtitle = post.author_first_name+", " +post.author_first_name;
                    noticeFeed.add(new Notice(post.notice_id,post.notice_title,subtitle, post.notice_body,post.author_id,post.channel_id, timestamp, URI.create(post.notice_image)));

                }
                updateListAdapter();

            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Crouton.makeText(getActivity(), "Sorry, Try again later", Style.ALERT)
                    .show();

        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (internetConnectionDetector.isConnectingToInternet()) {
            postIntentFilter = new IntentFilter();
            postIntentFilter.addAction("newPostArrived");
            getActivity().registerReceiver(postReceiver, postIntentFilter);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(postReceiver);

    }
}