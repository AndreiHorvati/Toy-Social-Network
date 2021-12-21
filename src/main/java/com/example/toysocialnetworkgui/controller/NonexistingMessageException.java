package com.example.toysocialnetworkgui.controller;

public class NonexistingMessageException extends RuntimeException {
    public NonexistingMessageException(String message) {
        super(message);
    }
}
