package com.jojo.flippy.adapter;

import java.net.URI;

public class AdminPerson {

    private URI adminProfileItem;
    private String profileEmail, profileFullName, adminId;

    public AdminPerson(URI profileChannelItem, String profileName, String profileFullName, String adminId) {
        this.adminProfileItem = profileChannelItem;
        this.profileEmail = profileName;
        this.profileFullName = profileFullName;
        this.adminId = adminId;
    }

    public void setProfileProfileFullName(String ProfileFullName) {
        this.profileFullName = ProfileFullName;
    }

    public URI getAdminProfileItem() {
        return adminProfileItem;
    }

    public void setAdminProfileItem(URI adminProfileItem) {
        this.adminProfileItem = adminProfileItem;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getProfileFullName() {
        return profileFullName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
