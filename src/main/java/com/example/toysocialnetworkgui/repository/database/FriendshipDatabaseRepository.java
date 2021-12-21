package com.example.toysocialnetworkgui.repository.database;

import com.example.toysocialnetworkgui.model.Friendship;
import com.example.toysocialnetworkgui.model.Tuple;
import com.example.toysocialnetworkgui.model.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDatabaseRepository extends AbstractDatabaseRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipDatabaseRepository(String url, String username, String password, Validator<Friendship> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        String sqlQuery = "insert into \"Friendship\"(id1, id2, initial_date) values(?, ?, ?)";

        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();
        LocalDateTime date = entity.getDate();

        if (findOne(new Tuple<>(id1, id2)) == null && findOne(new Tuple<>(id2, id1)) == null) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

                preparedStatement.setLong(1, entity.getId().getLeft());
                preparedStatement.setLong(2, entity.getId().getRight());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
                preparedStatement.executeUpdate();

                return null;
            } catch (SQLException throwables) {
                throw new DatabaseException("Eroare la baza de date!");
            }
        }

        return entity;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        Friendship friendship = findOne(id);
        String sqlQuery = "delete from \"Friendship\" where id1=? and id2=?";

        if (friendship != null) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

                preparedStatement.setLong(1, friendship.getId().getLeft());
                preparedStatement.setLong(2, friendship.getId().getRight());

                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throw new DatabaseException("Eroare la baza de date!");
            }
        }

        return friendship;
    }

    @Override
    public Friendship update(Friendship friendship) {
        if (friendship == null) {
            throw new IllegalArgumentException("Prietenia cautata nu poate fi nula!");
        }

        validator.validate(friendship);

        Friendship friendship2 = findOne(friendship.getId());
        String sqlQuery = "update \"Friendship\" set status=? where id1=? and id2=?";

        if (friendship2 != null && friendship2.getStatus().equals("pending")) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

                preparedStatement.setString(1, friendship.getStatus());
                preparedStatement.setLong(2, friendship.getId().getLeft());
                preparedStatement.setLong(3, friendship.getId().getRight());

                preparedStatement.executeUpdate();

                return null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return friendship;
    }

    private Friendship getFriendshipFromResultSet(ResultSet resultSet) {
        try {
            Long id1 = resultSet.getLong("id1");
            Long id2 = resultSet.getLong("id2");
            String status = resultSet.getString("status");
            LocalDateTime date = resultSet.getTimestamp("initial_date").toLocalDateTime();

            Friendship friendship = new Friendship();

            friendship.setId(new Tuple<>(id1, id2));
            friendship.setStatus(status);
            friendship.setDate(date);

            return friendship;
        } catch (SQLException e) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        String sqlQuery = "select * from \"Friendship\" where id1=? and id2=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setLong(1, id.getLeft());
            preparedStatement.setLong(2, id.getRight());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return getFriendshipFromResultSet(resultSet);
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sqlQuery = "select * from \"Friendship\"";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                friendships.add(getFriendshipFromResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }

        return friendships;
    }
}
