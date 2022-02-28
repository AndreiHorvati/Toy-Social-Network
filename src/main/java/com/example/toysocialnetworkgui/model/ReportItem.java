package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;

public class ReportItem {
    LocalDateTime date;
    String content;

    public ReportItem(String content, LocalDateTime date) {
        this.content = content;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
