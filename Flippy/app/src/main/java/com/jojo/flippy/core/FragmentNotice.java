package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.Notice;
import com.jojo.flippy.adapter.NoticeAdapter;
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
    private BroadcastReceiver mReceiver;
    private ListView noticeList;
    private NoticeAdapter listAdapter;
    private List<Notice> noticeFeed;
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
    private Button buttonLoadOldPost;


    public FragmentNotice() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notice, container,
                false);

        View footer = inflater.inflate(R.layout.notice_list_footer, null);
        View header = inflater.inflate(R.layout.notice_list_header, null);

        noticeFeed = new ArrayList<Notice>();
        listAdapter = new NoticeAdapter(getActivity(), R.layout.notice_list_item, noticeFeed);

        noticeList = (ListView) view.findViewById(R.id.listViewNoticeList);
        textViewNoNotice = (TextView) view.findViewById(R.id.textViewNoNotice);
        textViewNoNotice.setVisibility(View.GONE);
        progressBarCommunityCenterLoader = (ProgressBar) view.findViewById(R.id.progressBarLoadNoticeData);
        noticeList.addHeaderView(header);
        noticeList.addFooterView(footer);
        noticeList.setAdapter(listAdapter);
        buttonLoadOldPost = (Button) view.findViewById(R.id.buttonLoadOldPost);
        buttonLoadOldPost.setVisibility(View.GONE);
        TextView textViewOfflineMode = (TextView) view.findViewById(R.id.textViewOfflineMode);
        textViewOfflineMode.setVisibility(View.GONE);

        internetConnectionDetector = new InternetConnectionDetector(getActivity());
        if (!internetConnectionDetector.isConnectingToInternet()) {
            textViewOfflineMode.setVisibility(View.VISIBLE);
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

        return view;
    }

    private void updateListAdapter() {
        listAdapter.notifyDataSetChanged();
        if (listAdapter.isEmpty()) {
            textViewNoNotice.setVisibility(View.VISIBLE);
            textViewNoNotice.setText("Currently no notice, subscribe to receive notice");
        }
        if (listAdapter.getCount() >= 20) {
            buttonLoadOldPost.setVisibility(View.VISIBLE);
        }
    }


    private void persistPost(
            String notice_id, String notice_title, String notice_body, String notice_image, String start_date, String author_email, String author_id, String author_first_name, String author_last_name, String authorAvatar, String authorAvatarThumb, String channel_id) {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            Calendar calendar = Calendar.getInstance();
            Post post = new Post(notice_id, notice_title, notice_body, notice_image, start_date, author_email, author_id, author_first_name, author_last_name, authorAvatar, authorAvatarThumb, channel_id, calendar.getTimeInMillis());
            postDao.create(post);

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            ToastMessages.showToastLong(getActivity(), "Sorry, Failed to save post");

        }

    }

    private void getAllPost(final View view) {
        String url = Flippy.POST_URL;
        Ion.with(getActivity())
                .load(url)
                .setTimeout(60 * 60 * 1000)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarCommunityCenterLoader.setVisibility(view.GONE);

                        try {

                            if (result != null && result.has("results")) {
                                JsonArray communityArray = result.getAsJsonArray("results");
                                for (int i = 0; i < communityArray.size(); i++) {
                                    JsonObject item = communityArray.get(i).getAsJsonObject();
                                    JsonObject author = item.getAsJsonObject("author");
                                    String startDate = item.get("timestamp").getAsString();
                                    String title = item.get("title").getAsString().trim();
                                    String id = item.get("id").getAsString();
                                    String content = item.get("content").getAsString();
                                    String channel = item.get("channel").getAsString();
                                    String image_link = "";
                                    if (!item.get("image_url").isJsonNull()) {
                                        image_link = item.get("image_url").getAsString();
                                    }
                                    String authorEmail = author.get("email").getAsString();
                                    String authorAvatarThumb = "";
                                    String authorAvatar = "";
                                    if (!author.get("avatar_thumb").isJsonNull() && !author.get("avatar").isJsonNull()) {
                                        authorAvatarThumb = author.get("avatar_thumb").getAsString();
                                        authorAvatar = author.get("avatar").getAsString();
                                    }
                                    String authorId = author.get("id").getAsString();
                                    String authorFirstName = author.get("first_name").getAsString();
                                    String authorLastName = author.get("last_name").getAsString();
                                    persistPost(id, title, content, image_link, startDate, authorEmail, authorId, authorFirstName, authorLastName, authorAvatar, authorAvatarThumb, channel);

                                }
                                //after persistence load from database
                                loadAdapterFromDatabase(view);
                            } else {
                                Log.e("Fragment Notice", result.toString());
                            }
                            if (e != null) {
                                Log.e("Fragment Notice", e.toString());
                                return;
                            }

                        } catch (Exception exception) {
                            Log.e("Fragment Notice", exception.toString());
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
            noticeFeed.clear();
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
                        Log.e("Date error", error.toString());
                    }
                    String subtitle = post.author_first_name + ", " + post.author_last_name;
                    noticeFeed.add(new Notice(post.notice_id, post.notice_title, subtitle, post.notice_body, post.author_id, post.channel_id, timestamp, URI.create(post.author_avatar_thumb), URI.create(post.notice_image)));

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
        IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.MAIN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String RECEIVED = intent.getStringExtra("NEW_POST");
                Log.i("Fragment Notice", RECEIVED);
            }
        };
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
    }
}