package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event extends Entity<Long> {
    User creator;
    String name;
    String description;
    String location;
    LocalDateTime date;
    List<User> participants;

    public Event(User creator, String name, String description, String location, LocalDateTime date) {
        this.creator = creator;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;

        this.participants = new ArrayList<>();
    }

    public User getCreator() {
        return this.creator;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public List<User> getParticipants() {
        return this.participants;
    }

    public boolean isSubscribed(User user) {
        return participants.contains(user);
    }

    public void unsubscribe(User user) {
        this.participants.remove(user);
    }

    public void subscribe(User user) {
        this.participants.add(user);
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}
