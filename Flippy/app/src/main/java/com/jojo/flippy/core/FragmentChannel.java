package com.jojo.flippy.core;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.ManageChannelActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentChannel extends Fragment {
    //Instance of the user channel
    ListView ChannelListView;
    //Instance of the channel item
    List<Channel> rowItems;

    private Intent intent;
    private String channelName = "SRC channel";
    private  String totalMembers = "125";
    private Button buttonAddChannel,buttonManageChannel;
    private String isManageActivity = "false";



    public FragmentChannel() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_channel, container,
                false);

        intent = new Intent();
        //Loading the list with a dummy data
        rowItems = new ArrayList<Channel>();
        Channel item = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "GESA KNUST", "200 members","active");
        Channel item1 = new Channel(URI.create("http://www.ugsrc.com/wp-content/uploads/2013/08/6.jpg"), "SRC Legon, 2015", "4000 members","admin");
        rowItems.add(item);
        rowItems.add(item1);
        rowItems.add(item);
        rowItems.add(item1);


        ChannelListView = (ListView)view.findViewById(R.id.listViewChannels);
        ChannelAdapter adapter = new ChannelAdapter(getActivity(),
                R.layout.channel_listview, rowItems,true);
        ChannelListView.setAdapter(adapter);

        //Setting the click listener of the list view, if user clicks on a particular channel
        ChannelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
              //setting the click action for each of the items
                intent.setClass(getActivity(), ChannelMembers.class);
                intent.putExtra("channelName",channelName);
                intent.putExtra("totalMembers",totalMembers);
                intent.putExtra("isManageActivity",isManageActivity);
                startActivity(intent);


            }
        });

        buttonAddChannel = (Button)view.findViewById(R.id.buttonAddChannel);
        buttonManageChannel = (Button)view.findViewById(R.id.buttonManageChannel);
        buttonAddChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(), CreateChannelActivity.class);
                startActivity(intent);
            }
        });
        buttonManageChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] channelList = {"GESA KNUST", "SRC Legon", "Flippy Group","Another Group"};
                channelListDialog(channelList);
                return;
            }
        });

        return view;
    }
    private void channelListDialog(final CharSequence[] channelList) {
        //TODO this should line should return a list of user channels subscribed to
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_channel_list_dialog_title);
        builder.setItems(channelList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //get the selected option and pass it on to the next activity
                String channelToCreateNotice = channelList[item].toString();
                Intent intent = new Intent(getActivity().getApplication(),ManageChannelActivity.class);
                intent.putExtra("channelToCreateNotice",channelToCreateNotice);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}