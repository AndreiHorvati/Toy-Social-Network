package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private String friendFirstName;
    private String friendLastName;
    private String friendUsername;
    private LocalDateTime date;

    public FriendshipDTO(String friendFirstName, String friendLastName, String friendUsername, LocalDateTime date) {
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.friendUsername = friendUsername;
        this.date = date;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return friendFirstName + " | " + friendLastName + " | " + date.toLocalDate();
    }
}
