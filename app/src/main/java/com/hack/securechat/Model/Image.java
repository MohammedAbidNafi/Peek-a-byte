package com.hack.securechat.Model;

public class Image {

    String sender;

    String receiver;

    String imageUrl;

    String Password;

    String timestamp;

    String isSeen;

    String type;

    String thumbnail;

    String message;




    public Image(String sender, String receiver, String imageUrl,String type,String message, String password, String timestamp,String thumbnail, String isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.imageUrl = imageUrl;
        this.type = type;
        this.thumbnail = thumbnail;
        this.Password = password;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
        this.message = message;
    }

    public Image() {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}
