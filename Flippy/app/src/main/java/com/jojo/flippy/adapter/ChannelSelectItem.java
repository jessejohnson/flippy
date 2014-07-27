package com.jojo.flippy.adapter;

import java.net.URI;

public class ChannelSelectItem {
    private URI imageUrl;
    private String channelName;
    private String channelBio;
    private String id;

    public ChannelSelectItem(URI imageUrl, String id, String channelName, String channelBio) {
        this.imageUrl = imageUrl;
        this.channelName = channelName;
        this.channelBio = channelBio;
        this.id = id;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChannelBio() {
        return channelBio;
    }

    public void setChannelBio(String channelBio) {
        this.channelBio = channelBio;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return channelName + "\n" + imageUrl + "\n" + " " + channelBio;
    }
}