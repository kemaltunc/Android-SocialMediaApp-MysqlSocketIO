package com.example.kemal.seniorproject.Model;

public class Message {
    private String senderId;
    private String receiverId;
    private String conversationId;
    private String message;
    private String date;
    private String otherUserId;
    private String name;
    private String userImage;


    private String messageType;
    private String companyId;

    public Message() {
    }

    public Message(String conversationId) {
        this.conversationId = conversationId;
    }


    public Message(String senderId, String message, String date, String messageType) {

        this.senderId = senderId;
        this.message = message;
        this.date = date;
        this.messageType = messageType;


    }

    public Message(String senderId, String conversationId, String message, String date, String otherUserId, String name, String userImage, String messageType) {
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.message = message;
        this.date = date;
        this.otherUserId = otherUserId;
        this.name = name;
        this.userImage = userImage;
        this.messageType = messageType;
    }

    public Message(String senderId, String userImage, String name, String message, String date) {
        this.senderId = senderId;
        this.userImage = userImage;
        this.name = name;
        this.message = message;
        this.date = date;
    }

    public Message(String companyId, String conversationId, String senderId, String userImage, String name, String message, String date) {
        this.companyId = companyId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.userImage = userImage;
        this.name = name;
        this.message = message;
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
