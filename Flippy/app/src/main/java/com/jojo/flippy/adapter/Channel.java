package com.jojo.flippy.adapter;

import java.net.URI;

public class Channel {
    private URI imageUrl;
    private String channelName;
    private String creatorEmail;
    private String creatorFullName;
    private String id;

    public Channel(URI imageUrl, String id, String channelName, String creatorEmail, String creatorFullName) {
        this.imageUrl = imageUrl;
        this.channelName = channelName;
        this.creatorEmail = creatorEmail;
        this.creatorFullName = creatorFullName;
        this.id = id;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getCreatorFullName() {
        return creatorFullName;
    }

    public void setCreatorFullName(String channelName) {
        this.creatorFullName = creatorFullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return channelName + "\n" + imageUrl + "\n" + " " + creatorFullName;
    }
}