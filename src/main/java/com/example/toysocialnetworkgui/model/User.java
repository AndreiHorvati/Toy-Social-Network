package com.example.toysocialnetworkgui.model;

import java.util.Objects;

/**
 * Clasa care implementeaza un utilizator
 * Extinde clasa generica Entity
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    //private List<User> friends;

    /**
     * Constructorul utilizatorului
     * @param firstName - prenumele utilizatorului
     * @param lastName - numele utilizatorului
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        //friends = new ArrayList<>();
    }

    /**
     * Adauga un prieten la lista de prieteni a userului
     * @param friend - prietenul adaugat
     */
    //public void addFriend(User friend) {
        //this.friends.add(friend);
    //}

    /**
     * Sterge un prieten din lista de prieteni a utilizatorului
     * @param friend - prietenul care trebuie sa fie sters
     */
    //public void deleteFriend(User friend) {
        //this.friends.removeIf(user -> user.getId().equals(friend.getId()));
    //}

    /**
     * Da o valoare listei de prieteni a utlizatorului
     * @param newFriends - noua lista de prieteni
     */
    //public void setFriends(List<User> newFriends) {
        //this.friends = newFriends;
    //}

    /**
     * Getter pentru lista de prieteni a utilizatorului
     * @return lista de prieteni
     */
    //public List<User> getFriends() {
        //return friends;
    //}

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
