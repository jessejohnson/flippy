package com.jojo.flippy.core;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jojo.flippy.adapter.SettingsAdapter;
import com.jojo.flippy.adapter.SettingsItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.EditProfileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/10/14.
 */

public class FragmentSettings extends Fragment {
    private ListView listViewSettings;
    //Instance of the channel item
    List<SettingsItem> SettingsItems;

    private Intent intent;


    public FragmentSettings() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);

        intent = new Intent();

        //Loading the list with a dummy data
        SettingsItems = new ArrayList<SettingsItem>();
        SettingsItem settingHelp = new SettingsItem(R.drawable.ic_action_help, getResources().getString(R.string.settings_help), getResources().getString(R.string.settings_help_subtitle));
        SettingsItem settingProfile = new SettingsItem(R.drawable.ic_action_person_dark, getResources().getString(R.string.settings_profile), getResources().getString(R.string.settings_profile_subtitle));
        SettingsItem settingCommunity = new SettingsItem(R.drawable.ic_action_group_dark, getResources().getString(R.string.settings_accessibility), getResources().getString(R.string.settings_accessibility_subtitle));
        SettingsItem settingNotifications = new SettingsItem(R.drawable.ic_action_error, getResources().getString(R.string.settings_legal), getResources().getString(R.string.settings_legal_subtitle));
        SettingsItem settingAbout = new SettingsItem(R.drawable.ic_action_about, getResources().getString(R.string.settings_about), getResources().getString(R.string.settings_about_subtitle));
        SettingsItems.add(settingHelp);
        SettingsItems.add(settingProfile);
        SettingsItems.add(settingCommunity);
        SettingsItems.add(settingNotifications);
        SettingsItems.add(settingAbout);


        listViewSettings = (ListView) view.findViewById(R.id.listViewSettings);
        SettingsAdapter adapter = new SettingsAdapter(getActivity(),
                R.layout.settings_list_item, SettingsItems);
        listViewSettings.setAdapter(adapter);

        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                switch (position) {
                    case 0:
                        intent.setClass(getActivity(), HelpActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(getActivity(), EditProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(getActivity(), GeneralSettingsActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent.setClass(getActivity(), SettingsLegalActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(getActivity(), AboutActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        break;

                }

            }
        });
        return view;
    }

}