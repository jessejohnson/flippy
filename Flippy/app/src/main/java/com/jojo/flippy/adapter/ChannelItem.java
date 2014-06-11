package com.jojo.flippy.adapter;

/**
 * Created by bright on 6/11/14.
 */
public class ChannelItem {
    private int imageId;
    private String channelName;
    private String status;
    private String members;

    public ChannelItem(int imageId, String channelName, String status,String members) {
        this.imageId = imageId;
        this.channelName = channelName;
        this.status = status;
        this.members = members;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
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