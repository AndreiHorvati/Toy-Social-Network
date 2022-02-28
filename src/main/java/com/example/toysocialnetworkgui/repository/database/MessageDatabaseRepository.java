package com.example.toysocialnetworkgui.repository.database;

import com.example.toysocialnetworkgui.model.Message;
import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.model.UserIdForChatDTO;
import com.example.toysocialnetworkgui.model.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDatabaseRepository extends AbstractDatabaseRepository<Long, Message> {
    public MessageDatabaseRepository(String url, String username, String password, Validator<Message> validator) {
        super(url, username, password, validator);
    }

    private User getSenderFromResultSet(ResultSet resultSet) {
        try {
            Long senderId = resultSet.getLong("sender_id");
            String senderFirstName = resultSet.getString("sender_first_name");
            String senderLastName = resultSet.getString("sender_last_name");
            String senderUsername = resultSet.getString("sender_username");
            User sender = new User(senderFirstName, senderLastName, senderUsername);
            sender.setId(senderId);

            return sender;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    private User getReceiverFromResultSet(ResultSet resultSet) {
        try {
            Long receiverId = resultSet.getLong("receiver_id");
            String receiverFirstName = resultSet.getString("receiver_first_name");
            String receiverLastName = resultSet.getString("receiver_last_name");
            String receiverUsername = resultSet.getString("receiver_username");
            User receiver = new User(receiverFirstName, receiverLastName, receiverUsername);
            receiver.setId(receiverId);

            return receiver;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    private Message getMessageFromResultSet(ResultSet resultSet) {
        try {
            User sender = getSenderFromResultSet(resultSet);
            User receiver = getReceiverFromResultSet(resultSet);

            String message = resultSet.getString("message");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            Long replyId = resultSet.getLong("reply");

            Message reply;

            if (replyId == 0) {
                reply = null;
            } else {
                reply = findOne(replyId);
            }

            List<User> to = new ArrayList<>();
            to.add(receiver);

            Message message1 = new Message(sender, to, message);
            message1.setDate(date);
            message1.setReply(reply);
            message1.setId(resultSet.getLong("message_id"));

            return message1;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Message findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        String sqlQuery = "select s.username as sender_username, r.username as receiver_username, rm.id as message_id, sm.sender as sender_id, s.first_name as sender_first_name, s.last_name as sender_last_name, rm.receiver as receiver_id, " +
                "r.first_name as receiver_first_name, r.last_name as receiver_last_name, sm.message, sm.date, sm.reply from \"ReceiverMessage\" as rm inner join " +
                "\"SenderMessage\" as sm on RM.message_id = sm.id " +
                "inner join \"User\" as s on sm.sender = s.id " +
                "inner join \"User\" as r on rm.receiver = r.id " +
                "where rm.id= ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return getMessageFromResultSet(resultSet);
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    public ArrayList<Long> getIdsForReplyAll(Long messageId, Long userId) {
        ArrayList<Long> ids = new ArrayList<>();

        String sqlQueryGetReceivers = "select rm.receiver from \"ReceiverMessage\" as rm " +
                "where rm.message_id = (select message_id from \"ReceiverMessage\" as rm where " +
                "rm.id = ?) and rm.receiver != ?";

        String sqlQueryGetSender = "select sm.sender from \"SenderMessage\" as sm inner join " +
                "\"ReceiverMessage\" as rm on sm.id = rm.message_id where rm.id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatementReceivers = connection.prepareStatement(sqlQueryGetReceivers);
             PreparedStatement preparedStatementSender = connection.prepareStatement(sqlQueryGetSender)) {

            preparedStatementReceivers.setLong(1, messageId);
            preparedStatementReceivers.setLong(2, userId);

            ResultSet resultSetReceivers = preparedStatementReceivers.executeQuery();

            while (resultSetReceivers.next()) {
                ids.add(resultSetReceivers.getLong("receiver"));
            }

            preparedStatementSender.setLong(1, messageId);

            ResultSet resultSetSender = preparedStatementSender.executeQuery();

            if (resultSetSender.next()) {
                ids.add(resultSetSender.getLong("sender"));
            }

            return ids;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        String sqlQuery = "select s.username as sender_username, r.username as receiver_username, rm.id as message_id, sm.sender as sender_id, s.first_name as sender_first_name, s.last_name as sender_last_name, rm.receiver as receiver_id, " +
                "r.first_name as receiver_first_name, r.last_name as receiver_last_name, sm.message, sm.date, sm.reply from \"ReceiverMessage\" as rm inner join " +
                "\"SenderMessage\" as sm on RM.message_id = sm.id " +
                "inner join \"User\" as s on sm.sender = s.id " +
                "inner join \"User\" as r on rm.receiver = r.id ";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                messages.add(getMessageFromResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }

        return messages;
    }

    public List<Long> getAllUsersIdsForConversations(Long userId) {
        String sqlSenderQuery = "select s.sender_id, s.date from (select sm.sender as sender_id, sm.date as date " +
                "from \"ReceiverMessage\" as rm inner join " +
                "\"SenderMessage\" as sm on RM.message_id = sm.id " +
                "inner join \"User\" as s on sm.sender = s.id " +
                "inner join \"User\" as r on rm.receiver = r.id " +
                "where sm.sender = ? or rm.receiver = ? " +
                "order by sm.date) as s where sender_id != ?";

        String sqlReceiverQuery = "select s.receiver_id, s.date from (select rm.receiver as receiver_id, sm.date as date " +
                "from \"ReceiverMessage\" as rm inner join " +
                "\"SenderMessage\" as sm on RM.message_id = sm.id " +
                "inner join \"User\" as s on sm.sender = s.id " +
                "inner join \"User\" as r on rm.receiver = r.id " +
                "where sm.sender = ? or rm.receiver = ? " +
                "order by sm.date) as s where receiver_id != ?";

        List<UserIdForChatDTO> dtos = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatementSender = connection.prepareStatement(sqlSenderQuery);
            PreparedStatement preparedStatementReceiver = connection.prepareStatement(sqlReceiverQuery);

            preparedStatementSender.setLong(1, userId);
            preparedStatementSender.setLong(2, userId);
            preparedStatementSender.setLong(3, userId);

            preparedStatementReceiver.setLong(1, userId);
            preparedStatementReceiver.setLong(2, userId);
            preparedStatementReceiver.setLong(3, userId);

            ResultSet resultSetSender = preparedStatementSender.executeQuery();
            ResultSet resultSetReceiver = preparedStatementReceiver.executeQuery();

            while (resultSetSender.next()) {
                Long id = resultSetSender.getLong("sender_id");
                LocalDateTime date = resultSetSender.getTimestamp("date").toLocalDateTime();

                dtos.add(new UserIdForChatDTO(id, date));
            }

            while (resultSetReceiver.next()) {
                Long id = resultSetReceiver.getLong("receiver_id");
                LocalDateTime date = resultSetReceiver.getTimestamp("date").toLocalDateTime();

                dtos.add(new UserIdForChatDTO(id, date));
            }

            return dtos.stream().sorted((o1, o2) -> (o1.getDate().compareTo(o2.getDate())) * -1).map(UserIdForChatDTO::getId).distinct().collect(Collectors.toList());
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    public ArrayList<Message> getConversationBetween2Users(User user1, User user2) {
        String sqlQueryConversation = "select s.username as sender_username, r.username as receiver_username, rm.id as message_id, sm.sender as sender_id, s.first_name as sender_first_name, s.last_name as sender_last_name, rm.receiver as receiver_id, " +
                "r.first_name as receiver_first_name, r.last_name as receiver_last_name, sm.message, sm.date, sm.reply from \"ReceiverMessage\" as rm inner join " +
                "\"SenderMessage\" as sm on RM.message_id = sm.id " +
                "inner join \"User\" as s on sm.sender = s.id " +
                "inner join \"User\" as r on rm.receiver = r.id " +
                "where (sm.sender = ? and rm.receiver = ?) or " +
                "(sm.sender = ? and rm.receiver = ?) order by " +
                "sm.date";

        ArrayList<Message> conversation = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryConversation);

            preparedStatement.setLong(1, user1.getId());
            preparedStatement.setLong(2, user2.getId());
            preparedStatement.setLong(3, user2.getId());
            preparedStatement.setLong(4, user1.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                conversation.add(getMessageFromResultSet(resultSet));
            }

            return conversation;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Message save(Message entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        String sqlQuerySendSenderMessageReplyNotNull = "insert into \"SenderMessage\"(sender, message, date, reply) values (?, ?, ?, ?) returning \"SenderMessage\".id";
        String sqlQuerySendSenderMessageReplyNull = "insert into \"SenderMessage\"(sender, message, date) values(?, ?, ?) returning \"SenderMessage\".id";

        String sqlQueryReceiverMessage = "insert into \"ReceiverMessage\"(receiver, message_id) values(?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatementSenderMessage;

            if (entity.getReply() == null) {
                preparedStatementSenderMessage = connection.prepareStatement(sqlQuerySendSenderMessageReplyNull);

                preparedStatementSenderMessage.setLong(1, entity.getFrom().getId());
                preparedStatementSenderMessage.setString(2, entity.getMessage());
                preparedStatementSenderMessage.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            } else {
                preparedStatementSenderMessage = connection.prepareStatement(sqlQuerySendSenderMessageReplyNotNull);

                preparedStatementSenderMessage.setLong(1, entity.getFrom().getId());
                preparedStatementSenderMessage.setString(2, entity.getMessage());
                preparedStatementSenderMessage.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
                preparedStatementSenderMessage.setLong(4, entity.getReply().getId());
            }

            ResultSet resultSetInsertSenderMessage = preparedStatementSenderMessage.executeQuery();
            resultSetInsertSenderMessage.next();
            int messageId = resultSetInsertSenderMessage.getInt(1);

            PreparedStatement preparedStatementReceiverMessage = connection.prepareStatement(sqlQueryReceiverMessage);

            for (User receiver : entity.getTo()) {
                preparedStatementReceiverMessage.setLong(1, receiver.getId());
                preparedStatementReceiverMessage.setLong(2, messageId);

                preparedStatementReceiverMessage.executeUpdate();
            }

            preparedStatementReceiverMessage.close();

            return null;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
}
