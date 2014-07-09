package com.jojo.flippy.adapter;

import java.net.URI;

/**
 * Created by bright on 6/11/14.
 */
public class Community {
    private URI imageUrl;
    private String communityName;
    private String communityBio;
    private String id;

    public Community(URI imageUrl, String id, String channelName,String communityBio) {
        this.imageUrl = imageUrl;
        this.communityName = channelName;
        this.communityBio = communityBio;
        this.id = id;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCommunityBio() {
        return communityBio;
    }

    public void setCommunityBio(String communityBio) {
        this.communityBio = communityBio;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return communityName + "\n" + id;
    }
}