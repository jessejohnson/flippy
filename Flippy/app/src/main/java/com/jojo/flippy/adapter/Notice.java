package com.jojo.flippy.adapter;

import java.net.URI;
import java.util.Date;

/**
 * Created by odette on 6/11/14.
 */
public class Notice {
    String id;
    String creatorId;
    String channelId;
    String title;
    String subtitle;
    String content;
    String dateInfo;
    URI imageUrl;
    URI creatorUrl;
    Date timeStamp;
    Date reminderDate;
    String location;


    public Notice(String id, String title, String subtitle, String content, String creatorId, String channelId, String dateInfo, URI imageUrl, URI creatorUrl) {
        this.id = id;
        this.creatorId = creatorId;
        this.channelId = channelId;
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.imageUrl = imageUrl;
        this.dateInfo = dateInfo;
        this.creatorUrl = creatorUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle(){
        return subtitle;
    }

    public void setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public URI getCreatorUrl() {
        return creatorUrl;
    }

    public void setCreatorUrl(URI creatorUrl) {
        this.creatorUrl = creatorUrl;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }
}
