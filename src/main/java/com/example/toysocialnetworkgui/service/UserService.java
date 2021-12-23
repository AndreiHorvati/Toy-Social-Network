package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.model.User;
import com.example.toysocialnetworkgui.repository.ExistingUserException;
import com.example.toysocialnetworkgui.repository.database.UserDatabaseRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service-ul care se ocupa de entitatea User
 * Extinde clasa abstracta BasicService
 */
public class UserService {
    private User currentUser;
    private UserDatabaseRepository repository;

    /**
     * Contructorul service-ului
     *
     * @param repository - repository-ul pentru care lucreaza service-ul
     */
    public UserService(UserDatabaseRepository repository) {
        this.repository = repository;
        currentUser = null;
    }

    /**
     * @return userul curent
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Salveaza un user pe baza numelor sale
     *
     * @param firstName - prenumele user-ului
     * @param lastName  - numele user-ului
     * @return utilizatorul daca nu a putut fi adaugat sau null daca a fost adaugat cu succes
     * @throws IllegalArgumentException daca firstName sau lastName sunt null
     * @throws ExistingUserException    daca exista deja un utilizator cu acest nume
     */
    public User save(String firstName, String lastName, String username, String password) {
        if (firstName == null || lastName == null || username == null || password == null) {
            throw new IllegalArgumentException("Atributele nu pot fi null!");
        }

        User user = new User(firstName, lastName, username);

        return repository.saveWithPassword(user, password);
    }

    /**
     * Schimba userul curent pe baza numelor sale
     *
     * @param firstName - prenumele user-ului
     * @param lastName  - numele user-ului
     * @throws NonExistingUserException daca nu exista niciun user cu acest nume
     */
    public void changeCurrentUser(String firstName, String lastName) {
        Long userId = getUserIdByName(firstName, lastName);

        try {
            currentUser = findOne(userId);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }
    }

    /**
     * cauta id-ul userului cu numele date
     *
     * @param firstName - prenumele user-ului
     * @param lastName  - numele user-ului
     * @return userl gasit sau null daca nu exista
     */
    public Long getUserIdByName(String firstName, String lastName) {
        for (User user : findAll()) {
            if (user.getFirstName().equals(firstName) && user.getLastName().equals(lastName)) {
                return user.getId();
            }
        }

        return null;
    }

    public ArrayList<User> getUsersByStringArray(List<String> names) {
        ArrayList<User> users = new ArrayList<>();

        names.forEach(name -> {
            String[] userNames = name.split("\\ ");

            if (userNames.length == 1) {
                throw new NonExistingUserException("Utilizatorul cautat nu exista!");
            }

            String userFirstName = userNames[0];
            String userLastName = userNames[1];

            Long userId = getUserIdByName(userFirstName, userLastName);

            if (userId == null) {
                throw new NonExistingUserException("Utilizatorul cautat nu exista!");
            }

            User user = findOne(userId);
            users.add(user);
        });

        return users;
    }

    /**
     * Sterge un utilizator dupa numele lui
     *
     * @param firstName - prenumele utilizatorului
     * @param lastName  - numele utilizatorului
     * @return utilizatorul daca a fost sters cu succes sau null in caz contrar
     */
    public User delete(String firstName, String lastName) {
        Long id = getUserIdByName(firstName, lastName);

        try {
            User user = repository.delete(id);

            if (user != null) {
                //deleteFromFriends(user);
            }

            return user;
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }
    }

    /**
     * creaza lista o lista de utilizatori pe baza unei liste de id-uri
     *
     * @param ids - id-utile utilizatorilor
     * @return lista de useri creata
     */
    public ArrayList<User> getUsersByIds(ArrayList<Integer> ids) {
        ArrayList<User> users = new ArrayList<>();

        for (int id : ids) {
            users.add(findOne((long) id));
        }

        return users;
    }

    /**
     * Cauta id-ul maxim de la useri
     *
     * @return id-ul maxim gasit
     */
    public int maxId() {
        Long max = -1L;

        for (User user : findAll()) {
            if (user.getId() > max) {
                max = user.getId();
            }
        }

        return Integer.parseInt(max.toString());
    }

    /**
     * Modifica numele unui utilizatoru pe baza numelui sau vechi
     *
     * @param oldFirstName - prenumele vechi al utilizatorului
     * @param oldLastName  - numele vechi al utilizatorului
     * @param newFirstName - prenumele nou al utilizatorului
     * @param newLastName  - numele nou al utilizatorului
     * @return null daca utilizatorul a fost modificat sau utilizatorul daca nu s-a putut modifica
     */
    public User update(String oldFirstName, String oldLastName, String oldUsername, String newFirstName, String newLastName) {
        if (newFirstName == null || newLastName == null) {
            throw new IllegalArgumentException("Noile nume nu pot fi null!");
        }

        try {
            Long oldUserId = getUserIdByName(oldFirstName, oldLastName);

            User newUser = new User(newFirstName, newLastName, oldUsername);
            newUser.setId(oldUserId);

            return repository.update(newUser);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }
    }

    public User findOne(Long id) {
        return repository.findOne(id);
    }

    public Iterable<User> findAll() {
        return repository.findAll();
    }

    public List<String> getFullNames() {
        List<String> fullNames = new ArrayList<>();

        findAll().forEach(user -> {
            String fullName = user.getFirstName() + " " + user.getLastName();
            fullNames.add(fullName);
        });

        return fullNames;
    }
}
