package com.example.toysocialnetworkgui.controller;

/**
 * Clasa care semnaleaza ca un prieten exista deja
 * Extinde clasa de exceptii RuntimeException
 */
public class ExistingFriendException extends RuntimeException {
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public ExistingFriendException(String message) {
        super(message);
    }
}
