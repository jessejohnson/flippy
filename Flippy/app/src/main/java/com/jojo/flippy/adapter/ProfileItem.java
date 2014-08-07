package com.jojo.flippy.adapter;


import java.net.URI;

/**
 * Created by odette on 6/14/14.
 */
public class ProfileItem {

    private URI profileChannelItem;
    private String profileChannelName,profileChannelTotalNumber,textViewMemberId;


    public ProfileItem(URI profileChannelItem ,String profileChannelName,String profileChannelTotalNumber,String MemberId ) {
        this.profileChannelItem = profileChannelItem;
        this.profileChannelName = profileChannelName;
        this.profileChannelTotalNumber = profileChannelTotalNumber;
        this.textViewMemberId = MemberId;
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

    public String getTextViewMemberId() {
        return textViewMemberId;
    }

    public void setTextViewMemberId(String textViewMemberId) {
        this.textViewMemberId = textViewMemberId;
    }
}
