package com.example.toysocialnetworkgui.controller;

/**
 * Clasa care semnaleaza ca un este acelasi user
 * Extinde clasa de exceptii RuntimeException
 */
public class SameUserException extends RuntimeException{
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public SameUserException(String message) {
        super(message);
    }
}
