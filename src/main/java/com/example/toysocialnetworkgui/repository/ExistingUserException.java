package com.example.toysocialnetworkgui.repository;

/**
 * Clasa care semnaleaza ca un user exista deja
 * Extinde clasa de exceptii RuntimeException
 */
public class ExistingUserException extends RuntimeException {
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public ExistingUserException(String message) {
        super(message);
    }
}
