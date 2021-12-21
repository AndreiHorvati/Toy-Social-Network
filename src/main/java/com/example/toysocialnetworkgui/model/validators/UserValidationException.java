package com.example.toysocialnetworkgui.model.validators;

/**
 * Clasa care semnaleaza ca un user nu este valid
 * Extinde clasa de exceptii RuntimeException
 */
public class UserValidationException extends RuntimeException {
    /**
     * Constructorul exceptiei
     * @param message - mesajul exceptiei
     */
    public UserValidationException(String message) {
        super(message);
    }
}
