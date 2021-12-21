package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private String friendFirstName;
    private String friendLastName;
    private LocalDateTime date;

    public FriendshipDTO(String friendFirstName, String friendLastName, LocalDateTime date) {
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.date = date;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return friendFirstName + " | " + friendLastName + " | " + date.toLocalDate();
    }
}
