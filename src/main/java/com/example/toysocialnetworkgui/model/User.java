package com.example.toysocialnetworkgui.model;

import java.util.Objects;

/**
 * Clasa care implementeaza un utilizator
 * Extinde clasa generica Entity
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String username;

    /**
     * Constructorul utilizatorului
     * @param firstName - prenumele utilizatorului
     * @param lastName - numele utilizatorului
     */
    public User(String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Getter pentru prenumele utilizatorului
     * @return prenumele utilizatorului
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter pentru numele utilizatorului
     * @return numele utilizatorului
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Transforma un utilizator in string
     * @return stringul calculat
     */
    @Override
    public String toString() {
        return "Prenume: " +
                getFirstName() +
                "\n" +
                "Nume: " +
                getLastName() +
                "\n";
    }

    /**
     * Verifica daca un utilizator este egal cu alt obiect
     * @param o - obiectul cu care face comparatia
     * @return - true daca sunt egali altfel false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof User user)) {
            return false;
        }

        return getFirstName().equals(user.getFirstName()) &&
                getLastName().equals(user.getLastName());
    }

    /**
     * @return hash code-ul utilizatorului
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}
