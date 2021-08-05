package com.tccbiko.bondi.groupchat;

public class GroupList {
    private String groupid;
    private String groupName;
    private String lastMessage;
    private String date;
    private String profilePhoto;

    public GroupList() {

    }

    public GroupList(String groupid, String groupName, String lastMessage, String date, String profilePhoto) {
        this.groupid = groupid;
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.date = date;
        this.profilePhoto = profilePhoto;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}

