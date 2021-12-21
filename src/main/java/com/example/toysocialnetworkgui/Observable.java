package com.example.toysocialnetworkgui;

public interface Observable {
    void addObserver(com.example.toysocialnetworkgui.Observer e);
    void removeObserver(com.example.toysocialnetworkgui.Observer e);
    void notifyObservers();
}
