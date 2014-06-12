package com.jojo.flippy.core;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jojo.flippy.adapter.ChannelItem;
import com.jojo.flippy.adapter.CustomChannel;
import com.jojo.flippy.adapter.SettingsAdapter;
import com.jojo.flippy.adapter.SettingsItem;
import com.jojo.flippy.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentSettings extends Fragment {
    private ListView listViewSettings;
    //Instance of the channel item
    List<SettingsItem> SettingsItems;


    public FragmentSettings() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        //Loading the list with a dummy data
        SettingsItems = new ArrayList<SettingsItem>();
        SettingsItem settingHelp = new SettingsItem(R.drawable.ic_action_person, getResources().getString(R.string.settings_help), "200 members");
        SettingsItem settingProfile = new SettingsItem(R.drawable.ic_action_settings, getResources().getString(R.string.settings_profile), "4000 members");
        SettingsItem settingNotifications = new SettingsItem(R.drawable.ic_action_new, getResources().getString(R.string.settings_account), "200 members");
        SettingsItem settingCommunity = new SettingsItem(R.drawable.ic_action_person, getResources().getString(R.string.settings_accessibility), "4000 members");
        SettingsItems.add(settingHelp);
        SettingsItems.add(settingProfile);
        SettingsItems.add(settingNotifications);
        SettingsItems.add(settingCommunity);


        listViewSettings = (ListView)view.findViewById(R.id.listViewSettings);
        SettingsAdapter adapter = new SettingsAdapter(getActivity(),
                R.layout.settings_list_item, SettingsItems);
        listViewSettings.setAdapter(adapter);

        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                switch(position){
                    case 0:
                        Toast.makeText(getActivity(), position + "", Toast.LENGTH_LONG).show();
                        break;
                    case 1:

                    default:
                        break;

                }

            }
        });
        return view;
    }

}