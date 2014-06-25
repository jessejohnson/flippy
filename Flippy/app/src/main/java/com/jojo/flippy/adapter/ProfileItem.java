package com.jojo.flippy.adapter;

import android.widget.ImageView;

import java.net.URI;

/**
 * Created by odette on 6/14/14.
 */
public class ProfileItem {

    private URI profileChannelItem;
    private String profileChannelName,profileChannelTotalNumber;


    public ProfileItem(URI profileChannelItem ,String profileChannelName,String profileChannelTotalNumber ) {
        this.profileChannelItem = profileChannelItem;
        this.profileChannelName = profileChannelName;
        this.profileChannelTotalNumber = profileChannelTotalNumber;
    }

    public void setProfileChannelItem(URI profileChannelItem) {
        this.profileChannelItem = profileChannelItem;
    }

    public void setProfileChannelName(String profileChannelName) {
        this.profileChannelName = profileChannelName;
    }

    public void setProfileChannelTotalNumber(String profileChannelTotalNumber) {
        this.profileChannelTotalNumber = profileChannelTotalNumber;
    }

    public URI getProfileChannelItem() {
        return profileChannelItem;
    }

    public String getProfileChannelName() {
        return profileChannelName;
    }

    public String getProfileChannelTotalNumber() {
        return profileChannelTotalNumber;
    }

}
