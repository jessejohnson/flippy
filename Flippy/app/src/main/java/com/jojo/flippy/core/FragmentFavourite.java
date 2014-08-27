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
import com.jojo.flippy.persistence.DeletedPosts;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.services.DataService;
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

public class FragmentFavourite extends Fragment {
    private BroadcastReceiver mReceiver;
    private ListView noticeList;
    private NoticeAdapter listAdapter;
    private List<Notice> noticeFeed;
    private Dao<Post, Integer> postDao;
    private Intent intent;
    private String noticeTitle;
    private String noticeSubtitle;
    private String noticeId;
    private String noticeBody;
    private View view;
    private TextView textViewNoFavouriteNotice;



    public FragmentFavourite() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_favourite, container,
                false);


        noticeFeed = new ArrayList<Notice>();
        listAdapter = new NoticeAdapter(getActivity(), R.layout.notice_list_item, noticeFeed);

        noticeList = (ListView) view.findViewById(R.id.listViewFavouriteNoticeList);

        textViewNoFavouriteNotice = (TextView) view.findViewById(R.id.textViewNoFavouriteNotice);
        textViewNoFavouriteNotice.setVisibility(View.GONE);


        loadAdapterFromDatabase();


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
        if (listAdapter.getCount() == 0) {
            textViewNoFavouriteNotice.setVisibility(View.VISIBLE);
            textViewNoFavouriteNotice.setText("Currently no rated notice, rate to save notices");
        }
    }


    private void loadAdapterFromDatabase() {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            noticeFeed.clear();
            if (!postList.isEmpty()) {
                for (int i = postList.size() - 1; i >= 0; i--) {
                    Post post = postList.get(i);
                    if (post.is_favourite) {


                        String[] timestampArray = post.start_date.replace("Z", "").split("T");
                        String timestamp = timestampArray[0] + " @ " + timestampArray[1].substring(0, 8);

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
                }
            }
            updateListAdapter();
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Crouton.makeText(getActivity(), "Sorry, Try again later", Style.ALERT)
                    .show();

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}