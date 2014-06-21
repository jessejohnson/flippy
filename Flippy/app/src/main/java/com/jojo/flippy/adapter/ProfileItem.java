package com.jojo.flippy.adapter;

import android.widget.ImageView;

import java.net.URI;

/**
 * Created by odette on 6/14/14.
 */
public class ProfileItem {

    private URI profileImageId;
<<<<<<< HEAD
    private int profileChannelItem;
    private String username,userEmail,userNumber,profileChannelName,profileChannelTotalNumber;


    public ProfileItem(URI profileImageId,int profileChannelItem ,String userName, String userEmail,String userNumber,String profileChannelName,String profileChannelTotalNumber ) {
=======
    private URI profileChannelItem;
    private String username,useremail,usernumber,profileChannelName,profileChannelTotalNumber;


    public ProfileItem(URI profileImageId,URI profileChannelItem ,String username, String useremail,String usernumber,String profileChannelName,String profileChannelTotalNumber ) {
>>>>>>> f8f7be44b1d9442652df7bd87d46af858a263677
        this.profileImageId = profileImageId;
        this.profileChannelItem = profileChannelItem;
        this.username = userName;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
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

    public void setProfileImageId(URI profileImageId) {
        this.profileImageId = profileImageId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public URI getProfileChannelItem() {
        return profileChannelItem;
    }

    public URI getProfileImageId() {
        return profileImageId;
    }

    public String getProfileChannelName() {
        return profileChannelName;
    }

    public String getProfileChannelTotalNumber() {
        return profileChannelTotalNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUsername() {
        return username;
    }

    public String getUserNumber() {
        return userNumber;
    }

}
