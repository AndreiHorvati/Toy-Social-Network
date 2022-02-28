package com.example.toysocialnetworkgui.repository.database;

import com.example.toysocialnetworkgui.model.Event;
import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.model.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDatabaseRepository extends AbstractDatabaseRepository<Long, Event> {
    public EventDatabaseRepository(String url, String username, String password, Validator<Event> validator) {
        super(url, username, password, validator);
    }

    private Event getEventFromResultSet(ResultSet resultSet) {
        try {
            Long creatorsId = resultSet.getLong("creator_id");
            String creatorsFirstName = resultSet.getString("first_name");
            String creatorsLastName = resultSet.getString("last_name");
            String creatorsUsername = resultSet.getString("username");

            User creator = new User(creatorsFirstName, creatorsLastName, creatorsUsername);
            creator.setId(creatorsId);

            Long eventsId = resultSet.getLong("id");
            String eventsName = resultSet.getString("name");
            String eventsDescription = resultSet.getString("description");
            String eventsLocation = resultSet.getString("location");
            LocalDateTime eventsDate = resultSet.getTimestamp("date").toLocalDateTime();

            Event event = new Event(creator, eventsName, eventsDescription, eventsLocation, eventsDate);
            event.setId(eventsId);
            event.setParticipants(getEventsParticipants(event));

            return event;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    private List<User> getEventsParticipants(Event event) {
        List<User> participants = new ArrayList<>();

        String sqlQuery = "select U.id, U.first_name, U.last_name, U.username from \"User\" as U " +
                "inner join \"EventUser\" as EU on U.id = EU.id_user where EU.id_event = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setLong(1, event.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");

                User user = new User(firstName, lastName, username);
                user.setId(id);

                participants.add(user);
            }

            return participants;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    public List<Event> subscribedEvents(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User-ul nu poate sa fie null!");
        }

        List<Event> events = new ArrayList<>();

        String sqlQuery = "select EU.id_event from \"EventUser\" as EU inner join \"User\" as U " +
                "on EU.id_user = U.id where U.id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setLong(1, user.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                events.add(findOne(resultSet.getLong("id_event")));
            }

            return events;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Event findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        String sqlQuery = "select E.id as id, E.name as name, E.description as description, E.location as location, " +
                "E.date as date, U.id as creator_id, U.first_name as first_name, U.last_name as last_name, " +
                "U.username as username from \"Event\" as E inner join \"User\" as U on E.id_creator = U.id where E.id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return getEventFromResultSet(resultSet);
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Iterable<Event> findAll() {
        List<Event> events = new ArrayList<>();

        String sqlQuery = "select E.id as id, E.name as name, E.description as description, E.location as location, " +
                "E.date as date, U.id as creator_id, U.first_name as first_name, U.last_name as last_name, " +
                "U.username as username from \"Event\" as E inner join \"User\" as U on E.id_creator = U.id";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                events.add(getEventFromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }

        return events;
    }

    public void subscribeToEvent(Long usersId, Long eventsId) {
        String sqlQuery = "insert into \"EventUser\"(id_event, id_user) " +
                "values(?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setLong(1, eventsId);
            preparedStatement.setLong(2, usersId);

            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    public void unsbscribeToEvent(Long usersId, Long eventsId) {
        String sqlQuery = "delete from \"EventUser\" where id_event = ? and id_user = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setLong(1, eventsId);
            preparedStatement.setLong(2, usersId);

            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Event save(Event entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        String sqlQuery = "insert into \"Event\"(id_creator, name, description, location, date) " +
                "values(?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setLong(1, entity.getCreator().getId());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getDescription());
            preparedStatement.setString(4, entity.getLocation());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(entity.getDate()));

            preparedStatement.executeUpdate();

            return null;
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }
}
