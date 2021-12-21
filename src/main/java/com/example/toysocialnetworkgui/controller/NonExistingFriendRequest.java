package com.example.toysocialnetworkgui.controller;

public class NonExistingFriendRequest extends RuntimeException{
    public NonExistingFriendRequest(String msg)
    {
        super(msg);
    }
}
