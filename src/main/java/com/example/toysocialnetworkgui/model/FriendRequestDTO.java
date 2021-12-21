package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    private String friendFirstName;
    private String friendLastName;
    private LocalDateTime date;
    private String status;

    public FriendRequestDTO(String friendFirstName, String friendLastName, LocalDateTime date,String status) {
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.date = date;
        this.status=status;
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
