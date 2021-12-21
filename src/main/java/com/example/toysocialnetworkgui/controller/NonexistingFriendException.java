package com.example.toysocialnetworkgui.controller;

/**
 * Clasa care semnaleaza ca un prieten nu exista
 * Extinde clasa de exceptii RuntimeException
 */
public class NonexistingFriendException extends RuntimeException {
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public NonexistingFriendException(String message) {
        super(message);
    }
}
