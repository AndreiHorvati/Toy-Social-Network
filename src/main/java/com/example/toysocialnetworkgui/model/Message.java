package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message reply;

    public Message(User from, List<User> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.reply = null;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Message message)) {
            return false;
        }

        return getFrom().equals(message.getFrom()) && getTo().equals(message.getTo())
                && getMessage().equals(message.getMessage()) &&
                getDate().equals(message.getDate()) &&
                getReply().equals(message.getReply());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getMessage(), getDate(), getReply());
    }

    @Override
    public String toString() {
        if(reply == null) {
            return getId() + " " + getFrom().getFirstName() + " " + getFrom().getLastName() + ": " +
                    getMessage() + "\n";
        }

        return getId() + " " + getFrom().getFirstName() + " " + getFrom().getLastName() + ": " +
                getMessage() + " (Reply: " + getReply().getMessage() + ")\n";
    }
}
