package com.example.toysocialnetworkgui.model;

import java.time.LocalDateTime;

/**
 * Clasa care implementeaza o prietenie
 * Extinde clasa generica Entity
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    LocalDateTime date;
    String status;

    /**
     * Constructorul prieteniei
     */
    public Friendship() {
        this.date=LocalDateTime.now();
        this.status="pending";
    }

    /**
     * Getter pentru data prieteniei
     * @return data prieteniei
     */
    public LocalDateTime getDate() {
        return date;
    }
    public String getStatus()
    {
        return status;
    }
    public void setDate(LocalDateTime date1)
    {
        this.date=date1;
    }
    public void setStatus(String stat)
    {
        this.status=stat;
    }
}
