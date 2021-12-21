package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.model.Message;
import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.repository.database.MessageDatabaseRepository;

import java.util.ArrayList;

public class MessageService {
    MessageDatabaseRepository repository;

    public MessageService(MessageDatabaseRepository repository) {
        this.repository = repository;
    }

    public Message sendMessageToUsers(User sender, String stringMessage, ArrayList<User> receivers) {
        Message message = new Message(sender, receivers, stringMessage);

        return repository.save(message);
    }

    public ArrayList<Message> getConversationBetween2Users(User user1, User user2) {
        return repository.getConversationBetween2Users(user1, user2);
    }

    public Message save(Message message) {
        return repository.save(message);
    }

    public Message findOne(Long id) {
        return repository.findOne(id);
    }

    public ArrayList<Long> getIdsForReplyAll(Long messageId, Long userId) {
        return repository.getIdsForReplyAll(messageId, userId);
    }
}
