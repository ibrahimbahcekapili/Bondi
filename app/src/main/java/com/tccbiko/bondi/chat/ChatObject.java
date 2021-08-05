package com.tccbiko.bondi.chat;

public class ChatObject {

    private String dateTime;
    private String dateDay;
    private String textMessage;
    private String type;
    private String sender;

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    private String receiver;



    public ChatObject(String dateTime, String dateDay, String textMessage, String type, String sender, String receiver) {
        this.dateTime = dateTime;
        this.textMessage = textMessage;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.dateDay=dateDay;

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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public ChatObject() {
    }
}
