package com.jojo.flippy.adapter;

import android.widget.ImageView;

/**
 * Created by odette on 6/14/14.
 */
public class ProfileItem {

    private int profileImageId,profileChannelItem;
    private String username,useremail,usernumber,profileChannelName,profileChannelTotalNumber;


    public ProfileItem(int profileImageId,int profileChannelItem ,String username, String useremail,String usernumber,String profileChannelName,String profileChannelTotalNumber ) {
        this.profileImageId = profileImageId;
        this.profileChannelItem = profileChannelItem;
        this.username = username;
        this.useremail = useremail;
        this.usernumber = usernumber;
        this.profileChannelName = profileChannelName;
        this.profileChannelTotalNumber = profileChannelTotalNumber;
    }

    public void setProfileChannelItem(int profileChannelItem) {
        this.profileChannelItem = profileChannelItem;
    }

    public void setProfileChannelName(String profileChannelName) {
        this.profileChannelName = profileChannelName;
    }

    public void setProfileChannelTotalNumber(String profileChannelTotalNumber) {
        this.profileChannelTotalNumber = profileChannelTotalNumber;
    }

    public void setProfileImageId(int profileImageId) {
        this.profileImageId = profileImageId;
    }

    public void setUserEmail(String userEmail) {
        this.useremail = useremail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserNumber(String userNumber) {
        this.usernumber = usernumber;
    }

    public int getProfileChannelItem() {
        return profileChannelItem;
    }

    public int getProfileImageId() {
        return profileImageId;
    }

    public String getProfileChannelName() {
        return profileChannelName;
    }

    public String getProfileChannelTotalNumber() {
        return profileChannelTotalNumber;
    }

    public String getUserEmail() {
        return useremail;
    }

    public String getUsername() {
        return username;
    }

    public String getUserNumber() {
        return usernumber;
    }

}
