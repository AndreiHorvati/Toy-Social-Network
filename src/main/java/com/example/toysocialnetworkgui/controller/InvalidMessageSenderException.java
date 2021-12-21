package com.example.toysocialnetworkgui.controller;

public class InvalidMessageSenderException extends RuntimeException {
    InvalidMessageSenderException(String message) {
        super(message);
    }
}
