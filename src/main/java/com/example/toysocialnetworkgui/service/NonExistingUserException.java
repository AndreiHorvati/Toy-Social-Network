package com.example.toysocialnetworkgui.service;

/**
 * Clasa care semnaleaza ca un utilizator nu exista
 * Extinde clasa de exceptii RuntimeException
 */
public class NonExistingUserException extends RuntimeException{
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public NonExistingUserException(String message) {
        super(message);
    }
}
