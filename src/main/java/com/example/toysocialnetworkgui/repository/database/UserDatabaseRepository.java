package com.example.toysocialnetworkgui.repository.database;

import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.model.validators.*;
import com.example.toysocialnetworkgui.repository.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseRepository extends AbstractDatabaseRepository<Long, User> {
    public UserDatabaseRepository(String url, String username, String password, Validator<User> validator) {
        super(url, username, password, validator);
    }

    @Override
    public User findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        String sqlQuery = "select * from \"User\" where id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Long userId = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName, lastName);
                user.setId(userId);

                resultSet.close();

                return user;
            } else {
                resultSet.close();

                return null;
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<User>();
        String sqlQuery = "select * from \"User\"";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException throwables) {
            throw new DatabaseException("Eroare la baza de date!");
        }

        return users;
    }

    @Override
    public User save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        String sqlQuery = "insert into \"User\"(first_name, last_name) values(?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());

            preparedStatement.executeUpdate();

            return null;
        }catch (SQLException e)
        {
            throw new DatabaseException("Eroare la baza de date!");
        }
        catch (Exception e) {
            if (e.getMessage().contains("unique_full_name")) {
                throw new ExistingUserException("Utilizatorul cu acest nume exista deja!");
            }
        }
        return entity;
    }

    @Override
    public User delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        User user = findOne(id);
        String sqlQuery = "delete from \"User\" where id=?";

        if (user != null) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

                preparedStatement.setLong(1, id);

                preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throw new DatabaseException("Eroare la baza de date!");
            }
        }

        return user;
    }

    @Override
    public User update(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        User user = findOne(entity.getId());
        String sqlQuery = "update \"User\" set first_name=?, last_name=? where id=?";

        if (user != null) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

                 preparedStatement.setString(1, entity.getFirstName());
                 preparedStatement.setString(2, entity.getLastName());
                 preparedStatement.setLong(3, entity.getId());

                 preparedStatement.executeUpdate();

                 return null;
            } catch (SQLException throwables) {
                throw new DatabaseException("Eroare la baza de date!");
            }
            catch (Exception e) {
                if (e.getMessage().contains("unique_full_name")) {
                    throw new ExistingUserException("Utilizatorul cu acest nume exista deja!");
                }
            }
        }

        return entity;
    }
}
