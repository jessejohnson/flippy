package com.jojo.flippy.adapter;

import java.net.URI;

/**
 * Created by bright on 6/11/14.
 */
public class Channel {
    private URI imageUrl;
    private String channelName;
    private String status;
    private String members;

    public Channel(URI imageUrl, String channelName, String status, String members) {
        this.imageUrl = imageUrl;
        this.channelName = channelName;
        this.status = status;
        this.members = members;
    }
    public URI getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public String getMembers() {
        return members;
    }
    public void setMembers(String channelName) {
        this.members = members;
    }
    @Override
    public String toString() {
        return channelName + "\n" + status + "\n" + " " + members;
    }
}