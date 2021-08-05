package com.tccbiko.bondi.user;

public class Groups {

    private String groupID;
    private String groupName;
    private String profilePhoto;

    public Groups() {
    }

    public Groups(String groupID, String groupName, String profilePhoto) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.profilePhoto = profilePhoto;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
