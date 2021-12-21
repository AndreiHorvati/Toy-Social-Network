package com.example.toysocialnetworkgui.service;

/**
 * Clasa care semnaleaza ca un utilizator nu exista
 * Extinde clasa de exceptii RuntimeException
 */
public class NonexistentUserException extends RuntimeException{
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public NonexistentUserException(String message) {
        super(message);
    }
}
