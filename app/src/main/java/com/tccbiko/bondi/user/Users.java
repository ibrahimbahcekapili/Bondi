package com.tccbiko.bondi.user;

public class Users {

    private String userID;
    private String userName;
    private String userPhone;
    private String profilePhoto;
    private String status;
    private String bio;

    private Boolean selected=false;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Users(String userID, String userName, String userPhone, String profilePhoto, String status, String bio) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.profilePhoto = profilePhoto;
        this.status = status;
        this.bio = bio;
    }

    public Users() {

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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
