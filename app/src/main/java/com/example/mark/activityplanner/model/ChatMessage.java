package com.example.mark.activityplanner.model;

/**
 * Created by Mark on 19/03/2018.
 */

public class ChatMessage {

    private String messageText, messageUser;
    private UserType userType;
    private Status messageStatus;

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    private long messageTime;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageUser(String messageUser){
        this.messageUser = messageUser;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {

        return messageText;
    }

    public String getMessageUser(){
        return messageUser;
    }

    public UserType getUserType() {
        return userType;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }
}