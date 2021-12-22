package com.example.toysocialnetworkgui.controller;

import com.example.toysocialnetworkgui.Observable;
import com.example.toysocialnetworkgui.Observer;
import com.example.toysocialnetworkgui.model.*;
import com.example.toysocialnetworkgui.service.*;
import com.example.toysocialnetworkgui.utils.Graph;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Clasa controller care delega responsabilitatea service-urilor
 */
public class Controller implements Observable {
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private AuthenticationService authenticationService;

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers() {
        observers.stream().forEach(Observer::update);
    }

    /**
     * Constructorul controllerului
     *
     * @param userService       - service-ul pentru useri
     * @param friendshipService - service-ul pentru prietenii
     */
    public Controller(UserService userService, FriendshipService friendshipService, MessageService messageService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.authenticationService = authenticationService;
    }

    public void login(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        authenticationService.login(username, password);
    }

    /**
     * @return utilizatorul curent
     */
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public int numberOfFriends(Long id) {
        int count = 0;

        for (Friendship friendship : friendshipService.findAll()) {
            if (friendship.getId().getLeft().equals(id) || friendship.getId().getRight().equals(id)) {
                count++;
            }
        }

        return count;
    }

    public ArrayList<User> getCurrentUsersFriends() {
        ArrayList<User> friends = new ArrayList<>();

        for (Friendship friendship : friendshipService.findAll()) {
            if (friendship.getId().getLeft().equals(getCurrentUser().getId())) {
                friends.add(userService.findOne(friendship.getId().getRight()));
            } else if (friendship.getId().getRight().equals(getCurrentUser().getId())) {
                friends.add(userService.findOne(friendship.getId().getLeft()));
            }
        }

        return friends;
    }

    /**
     * Salveaza un utilizator pe baza numelor sale
     *
     * @param firstName - prenumele utilizatorului
     * @param lastName  - numele utilizatorului
     * @return null daca utilizatorul a fost salvat altfel utilizatorul
     */
    public User saveUser(String firstName, String lastName, String username, String password) {
        return userService.save(firstName, lastName, username, password);
    }

    /**
     * Modifica utilizatorul curent pe baza numelor sale
     *
     * @param firstName - prenumele utilizatorului
     * @param lastName  - numele utilizatorului
     */
    public void changeCurrentUser(String firstName, String lastName) {
        userService.changeCurrentUser(firstName, lastName);
    }

    /**
     * Adauga un prieten utilizatorului curent
     *
     * @param firstName - prenumele prietenului
     * @param lastName  - numele prietenului
     * @throws NonExistingUserException daca nu exista un user cu acele nume
     */
    public void addFriendToCurrentUser(String firstName, String lastName) {
        try {
            addFriend(getCurrentUser(), firstName, lastName);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    /**
     * Adauga un prieten unui utlizator pe baza numelor
     *
     * @param user      - utilizatorul pentru care adaugam prietenul
     * @param firstName - prenumele prietenului
     * @param lastName  - numele prietenului
     */
    public void addFriend(User user, String firstName, String lastName) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        try {
            Long friendsId = userService.getUserIdByName(firstName, lastName);
            userService.findOne(friendsId);

            if (user.getId().equals(friendsId)) {
                throw new SameUserException("Nu te poti adauga ca prieten pe tine!");
            }

            if (friendshipService.findOne(new Tuple<>(user.getId(), friendsId)) != null ||
                    friendshipService.findOne(new Tuple<>(friendsId, user.getId())) != null) {
                throw new ExistingFriendException("Deja ai la prieteni acest utilizator!");
            }

            friendshipService.save(user.getId(), friendsId);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }
    }

    public void sendMessageToUsersFromCurrentUser(String message, List<String> names) {
        try {
            sendMessageToUsers(getCurrentUser(), message, names);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public void replyToMessage(User user1, String firstName, String lastName, String stringMessage, Long messageId) {
        if (user1 == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        Long user2Id = userService.getUserIdByName(firstName, lastName);

        if (userService.findOne(user1.getId()) == null ||
                user2Id == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        Message message = messageService.findOne(messageId);

        if (message == null) {
            throw new NonexistingMessageException("Mesajul cautat nu exista!");
        }

        if (!message.getFrom().getId().equals(user2Id) && !message.getTo().get(0).getId().equals(user2Id)) {
            throw new InvalidMessageSenderException("Acest mesaj nu apartine utilizatorului cautat!");
        }

        ArrayList<User> to = new ArrayList<>();
        to.add(userService.findOne(user2Id));

        Message replyMessage = new Message(user1, to, stringMessage);
        replyMessage.setReply(message);

        messageService.save(replyMessage);
    }

    public void replyToAll(User user1, String firstName, String lastName, String stringMessage, Long messageId) {
        if (user1 == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        Long user2Id = userService.getUserIdByName(firstName, lastName);

        if (userService.findOne(user1.getId()) == null ||
                user2Id == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        Message message = messageService.findOne(messageId);

        if (message == null) {
            throw new NonexistingMessageException("Mesajul cautat nu exista!");
        }

        if (!message.getFrom().getId().equals(user2Id) && !message.getTo().get(0).getId().equals(user2Id)) {
            throw new InvalidMessageSenderException("Acest mesaj nu apartine utilizatorului cautat!");
        }

        ArrayList<Long> toIds = messageService.getIdsForReplyAll(messageId, user1.getId());

        ArrayList<User> to = new ArrayList<>();

        toIds.forEach(id -> {
            to.add(userService.findOne(id));
        });

        Message replyMessage = new Message(user1, to, stringMessage);
        replyMessage.setReply(message);

        messageService.save(replyMessage);
    }

    public void currentUserReply(String firstMName, String lastName, String stringMessage, Long messageId) {
        try {
            replyToMessage(getCurrentUser(), firstMName, lastName, stringMessage, messageId);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public void currentUserReplyToAll(String firstMName, String lastName, String stringMessage, Long messageId) {
        try {
            replyToAll(getCurrentUser(), firstMName, lastName, stringMessage, messageId);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public void sendMessageToUsers(User user, String message, List<String> names) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        ArrayList<User> receivers = userService.getUsersByStringArray(names);
        messageService.sendMessageToUsers(user, message, receivers);
    }

    public ArrayList<Message> conversationOfTwoUsers(User user1, String firstName, String lastName) {
        if (user1 == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        Long user2Id = userService.getUserIdByName(firstName, lastName);

        if (userService.findOne(user1.getId()) == null ||
                user2Id == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        return messageService.getConversationBetween2Users(user1, userService.findOne(user2Id));
    }

    public Long getUserIdByName(String firstName, String lastName) {
        return userService.getUserIdByName(firstName, lastName);
    }

    public ArrayList<Message> currentUsersConversationWithAnotherUser(String firstName, String lastName) {
        try {
            return conversationOfTwoUsers(getCurrentUser(), firstName, lastName);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    /**
     * Sterge un utilizator pe baza numelui sau
     *
     * @param firstName - prenumele userului
     * @param lastName  - numele userului
     * @return userul daca a fost sters cu succes altfel null
     */
    public User deleteUser(String firstName, String lastName) {
        User user = userService.delete(firstName, lastName);

        if (user != null) {
            friendshipService.deleteFriends(user.getId());
        }

        return user;
    }

    /**
     * Modifica un utilizator
     *
     * @param oldFirstName - prenumele vechi al userului
     * @param oldLastName  - numele vechi al utilizatorului
     * @param newFirstName - prenumele nou al userului
     * @param newLastName  - numele nou al userului
     * @return null daca userul a fost modificat sau userul daca acesta nu a putut fi modifcat
     */
    public User updateCurrentUser(String oldFirstName, String oldLastName, String oldUsername, String newFirstName, String newLastName) {
        User user = userService.update(oldFirstName, oldLastName, oldUsername, newFirstName, newLastName);

        if (user == null) {
            userService.changeCurrentUser(newFirstName, newLastName);
        }

        return user;
    }

    /**
     * Sterge un prieten de la utilizatorul curent pe baza numelui sau
     *
     * @param firstName - prenumele prietenului
     * @param lastName  - numele prietenului
     */
    public void deleteFriend(User user, String firstName, String lastName) {
        if (user == null) {
            throw new IllegalArgumentException("Utilizatorul nu poate sa fie null!");
        }

        if (userService.getUserIdByName(user.getFirstName(), user.getLastName()) == null) {
            throw new NonExistingUserException("Utilizatorul nu exista!");
        }

        Long friendId = userService.getUserIdByName(firstName, lastName);
        if (friendId == null) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }

        int numberOfFriends = numberOfFriends(user.getId());
        friendshipService.deleteFriendship(user.getId(), userService.getUserIdByName(firstName, lastName));

        int newNumberOfFriends = numberOfFriends(user.getId());

        if (numberOfFriends == newNumberOfFriends) {
            throw new NonexistingFriendException("Prietenul cautat nu exista!");
        }
    }

    public void deleteFriendFromCurrentUser(String firstName, String lastName) {
        try {
            deleteFriend(getCurrentUser(), firstName, lastName);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    /**
     * @return id-ul maxim al unui utilizator
     */
    public int maxId() {
        return userService.maxId();
    }

    /**
     * @return numarul de comunitati
     */
    public int numberOfCommunities() {
        Graph networkGraph = new Graph(this);

        return networkGraph.connectedComponents();
    }

    /**
     * @return lantul de lungime maxima din cea mai sociabila comunitate
     */
    public Graph.Path theMostSociableCommunityPath() {
        Graph networkGraph = new Graph(this);

        return networkGraph.longestPath();
    }

    /**
     * @return id-urile celei mai sociabile comunitati
     */
    public ArrayList<Integer> theMostSociableCommunityIds() {
        Graph networkGraph = new Graph(this);

        return networkGraph.getTheMostSociableCommunity();
    }

    /**
     * @return cea mai sociabila comunitate
     */
    public ArrayList<User> theMostSociableCommunity() {
        return userService.getUsersByIds(theMostSociableCommunityIds());
    }

    /**
     * @return toate relatiile de prietenie
     */
    public Iterable<Friendship> findAllFriendships() {
        return friendshipService.findAll();
    }

    /**
     * Cauta un user dupa id
     *
     * @param id - id-ul userului cautat
     * @return null daca nu exista userul sau userul cautat
     */
    public User findUser(Long id) {
        return userService.findOne(id);
    }

    /**
     * @return toti utilizatorii
     */
    public Iterable<User> findAllUsers() {
        return userService.findAll();
    }

    public Friendship sendFriendRequest(User user, String firstName, String lastName) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate fi nul!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Nu exista userul!");
        }

        try {
            Long friendsId = userService.getUserIdByName(firstName, lastName);
            userService.findOne(friendsId);

            if (user.getId().equals(friendsId)) {
                throw new SameUserException("Nu te poti adauga ca prieten pe tine!");
            }

            Friendship friendship1 = friendshipService.findOne(new Tuple<>(user.getId(), friendsId));
            Friendship friendships2 = friendshipService.findOne(new Tuple<>(friendsId, user.getId()));

            if (friendship1 != null && friendship1.getStatus().equals("pending") || friendships2 != null && friendships2.getStatus().equals("pending")) {
                throw new ExistingFriendRequest("Cererea a mai fost trimisa!");
            }
            if (friendship1 != null && friendship1.getStatus().equals("approved") || friendships2 != null && friendships2.getStatus().equals("approved")) {
                throw new ExistingFriendException("Cei doi sunt deja prieteni!");
            }
            if (friendship1 != null && friendship1.getStatus().equals("rejected") || friendships2 != null && friendships2.getStatus().equals("rejected")) {
                throw new RejectionException("Ti-am respins deja cererea!");
            }

            return friendshipService.save(user.getId(), friendsId);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }
    }

    public Friendship changeFriendshipStatus(User user, String firstName, String lastName, String status) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate fi nul!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Nu exista userul!");
        }

        try {
            Long friendsId = userService.getUserIdByName(firstName, lastName);
            userService.findOne(friendsId);

            Friendship friendships2 = friendshipService.findOne(new Tuple<>(friendsId, user.getId()));

            if (friendships2 == null) {
                throw new NonExistingFriendRequest("Nu exista cererea!");
            }

            if (friendships2.getStatus().equals("approved")) {
                throw new ExistingFriendException("Deja exista relatia de prietenie!");
            }

            if (friendships2.getStatus().equals("rejected")) {
                throw new RejectionException("Cererea a fost deja respinsa!");
            }

            friendships2.setStatus(status);

            return friendshipService.updateFriend(friendships2);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }
    }

    public Friendship approveFriendRequest(User user, String firstName, String lastName) {
        Friendship f = changeFriendshipStatus(user, firstName, lastName, "approved");
        notifyObservers();

        return f;
    }

    public Friendship rejecteFriendRequest(User user, String firstName, String lastName) {
        return changeFriendshipStatus(user, firstName, lastName, "rejected");
    }

    public Friendship rejectFriendRequestFromCurrentUser(String firstName, String lastName) {
        try {
            return rejecteFriendRequest(getCurrentUser(), firstName, lastName);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Userul curent nu exista!");
        }
    }

    public Friendship approveFriendRequestFromCurrentUser(String firstName, String lastName) {
        try {
            return approveFriendRequest(getCurrentUser(), firstName, lastName);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Userul curent nu exista!");
        }
    }

    public Friendship sendFriendRequestFromCurrentUser(String firstName, String lastName) {
        try {
            return sendFriendRequest(getCurrentUser(), firstName, lastName);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Userul curent nu exista!");
        }
    }

    public List<FriendshipDTO> getAllFriendships(User user) {
        return getAllFriendshipsStream(user).collect(Collectors.toList());
    }

    public Stream<FriendshipDTO> getAllFriendshipsStream(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate fi vid!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Nu exista userul cautat!");
        }

        ArrayList<Friendship> friendships = (ArrayList<Friendship>) friendshipService.findAll();

        return friendships.stream().filter(x -> (x.getId().getLeft().equals(user.getId()) || x.getId().getRight().equals(user.getId())) &&
                x.getStatus().equals("approved")).map(x -> {
            String firstname;
            String lastname;
            if (x.getId().getLeft().equals(user.getId())) {
                firstname = userService.findOne(x.getId().getRight()).getFirstName();
                lastname = userService.findOne(x.getId().getRight()).getLastName();
            } else {
                firstname = userService.findOne(x.getId().getLeft()).getFirstName();
                lastname = userService.findOne(x.getId().getLeft()).getLastName();
            }
            return new FriendshipDTO(firstname, lastname, x.getDate());
        });
    }

    public List<FriendshipDTO> getAllFriendshipsByMonth(User user, int month) {
        if (month < 1 || month > 12) {
            throw new InvalidMonthException("Aceasta luna nu exista!");
        }

        return getAllFriendshipsStream(user).filter(x -> x.getDate().getMonth().getValue() == month)
                .collect(Collectors.toList());
    }

    public List<FriendshipDTO> getAllFriendshipsFromCurrentUserByMonth(int month) {
        try {
            return getAllFriendshipsByMonth(getCurrentUser(), month);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Userul curent nu exista!");
        }
    }

    public List<FriendshipDTO> getAllFriendshipsFromCurrentUser() {
        try {
            return getAllFriendships(getCurrentUser());
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Nu exista userul curent!");
        }
    }

    public Stream<FriendRequestDTO> getAllFriendRequestsStream(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate fi vid!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Nu exista userul cautat!");
        }

        ArrayList<Friendship> friendships = (ArrayList<Friendship>) friendshipService.findAll();

        return friendships.stream().filter(x -> (x.getId().getLeft().equals(user.getId()) || x.getId().getRight().equals(user.getId()))
        ).map(x -> {
            String firstname;
            String lastname;
            if (x.getId().getLeft().equals(user.getId())) {
                firstname = userService.findOne(x.getId().getRight()).getFirstName();
                lastname = userService.findOne(x.getId().getRight()).getLastName();
            } else {
                firstname = userService.findOne(x.getId().getLeft()).getFirstName();
                lastname = userService.findOne(x.getId().getLeft()).getLastName();
            }
            return new FriendRequestDTO(firstname, lastname, x.getDate(), x.getStatus());
        });
    }

    public List<FriendRequestDTO> getAllFriendRequests(User user) {
        return getAllFriendRequestsStream(user).collect(Collectors.toList());
    }

    public List<FriendRequestDTO> getAllFriendRequestsFromCurrentUser() {
        try {
            return getAllFriendRequests(getCurrentUser());
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Nu exista userul curent!");
        }
    }

}
