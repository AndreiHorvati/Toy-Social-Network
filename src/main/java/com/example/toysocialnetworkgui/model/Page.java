package com.example.toysocialnetworkgui.model;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private User user;
    private List<User> friends;

    public Page(User user) {
        this.user = user;
        friends = new ArrayList<>();
    }

    public User getUser() {
        return this.user;
    }

    public List<User> getFriends() {
        return this.friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
