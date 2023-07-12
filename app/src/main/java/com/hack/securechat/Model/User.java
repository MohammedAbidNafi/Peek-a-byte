package com.hack.securechat.Model;

public class User {

    String username;
    String id;

    String imageURL;



    String joined_on;


    public User(String username, String id, String imageURL, String joined_on) {
        this.username = username;
        this.id = id;
        this.imageURL = imageURL;

        this.joined_on = joined_on;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }



    public String getJoined_on() {
        return joined_on;
    }

    public void setJoined_on(String joined_on) {
        this.joined_on = joined_on;
    }
}
