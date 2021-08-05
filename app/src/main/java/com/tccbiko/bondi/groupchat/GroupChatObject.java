package com.tccbiko.bondi.groupchat;

public class GroupChatObject {

    private String dateTime;

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    private String dateDay;
    private String textMessage;
    private String type;
    private String sender;
    private String groupid;
    private String sendername;

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    private String pp;

    public GroupChatObject(String dateTime, String dateDay, String textMessage, String type, String sender, String groupid, String sendername, String pp) {
        this.dateTime = dateTime;
        this.textMessage = textMessage;
        this.type = type;
        this.sender = sender;
        this.groupid = groupid;
        this.dateDay=dateDay;
        this.sendername= sendername;
        this.pp=pp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public GroupChatObject() {
    }
}
