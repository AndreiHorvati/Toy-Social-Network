package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.model.Event;
import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.repository.database.EventDatabaseRepository;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    EventDatabaseRepository repository;

    public EventService(EventDatabaseRepository repository) {
        this.repository = repository;
    }

    public Event save(Event event) {
        return repository.save(event);
    }

    public Event findOne(Long id) {
        return repository.findOne(id);
    }

    public Iterable<Event> findAll() {
        return repository.findAll();
    }

    public void subscribeToEvent(User user, Event event) {
        repository.subscribeToEvent(user.getId(), event.getId());
    }

    public List<Event> subscribedEvents(User user) {
        return repository.subscribedEvents(user);
    }

    public void unsbscribeToEvent(User user, Event event) {
        repository.unsbscribeToEvent(user.getId(), event.getId());
    }
}
