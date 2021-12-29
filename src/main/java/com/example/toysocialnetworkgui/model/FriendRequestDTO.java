package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    private String friendFirstName;
    private String friendLastName;
    private String username;
    private LocalDateTime date;
    private String status;

    public FriendRequestDTO(String friendFirstName, String friendLastName, String username, LocalDateTime date,String status) {
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.username = username;
        this.date = date;
        this.status=status;
    }

    public String getUsername() {
        return username;
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

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return friendFirstName + " | " + friendLastName + " | " + date.toLocalDate()+ "|"+status;
    }
}
