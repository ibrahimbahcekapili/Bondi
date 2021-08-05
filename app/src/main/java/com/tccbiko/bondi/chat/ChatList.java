package com.tccbiko.bondi.chat;

public class ChatList {
    private String userID;
    private String userName;
    private String description;
    private String date;
    private String profilePhoto;


    public ChatList() {
    }




    public ChatList(String userID, String userName, String description, String date, String profilePhoto) {
        this.userID = userID;
        this.userName = userName;
        this.description = description;
        this.date = date;
        this.profilePhoto = profilePhoto;


    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
