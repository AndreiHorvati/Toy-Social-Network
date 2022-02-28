package com.example.toysocialnetworkgui.controller;

import com.example.toysocialnetworkgui.Observable;
import com.example.toysocialnetworkgui.Observer;
import com.example.toysocialnetworkgui.model.*;
import com.example.toysocialnetworkgui.service.*;
import com.example.toysocialnetworkgui.utils.Graph;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Clasa controller care delega responsabilitatea service-urilor
 */
public class Controller implements Observable {
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private AuthenticationService authenticationService;
    private EventService eventService;

    private List<Observer> observers = new ArrayList<>();

    public List<String> getUsersDetails() {
        return this.userService.getUsersDetails();
    }

    public User getUserByUsername(String username) {
        return this.userService.getUserByUsername(username);
    }

    public Page getUserPage(User user) {
        Page page = new Page(user);
        List<FriendshipDTO> friendshipDTOList = getAllFriendships(user);
        page.setFriends(convertFriendshipDTOListToUserList(friendshipDTOList));

        return page;
    }

    public List<Event> subscribedEvents(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        return eventService.subscribedEvents(user);
    }

    public List<User> convertFriendshipDTOListToUserList(List<FriendshipDTO> friendshipDTOList) {
        return friendshipDTOList.stream().map(friendshipDTO -> {
            return getUserByUsername(friendshipDTO.getFriendUsername());
        }).collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }

    /**
     * Constructorul controllerului
     *
     * @param userService       - service-ul pentru useri
     * @param friendshipService - service-ul pentru prietenii
     */
    public Controller(UserService userService, FriendshipService friendshipService, MessageService messageService, AuthenticationService authenticationService, EventService eventService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.authenticationService = authenticationService;
        this.eventService = eventService;
    }

    public String getProfilePicture(String username) {
        return userService.getProfilePicture(username);
    }

    public void login(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        authenticationService.login(username, password);
    }

    /**
     * @return utilizatorul curent
     */
    public User getCurrentUser() {
        return authenticationService.getCurrentUser();
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

    public Friendship getFriendshipBetweenCurrentUserAndAnotherUser(String username) {
        try {
            return getFriendshipBetweenTwoUsers(getCurrentUser(), username);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public void subscribeToEvent(User user, Event event) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        if (eventService.findOne(event.getId()) == null) {
            throw new NonExistingEventException("Eventul cautat nu exista!");
        }

        eventService.subscribeToEvent(user, event);
    }

    public void unsbscribeToEvent(User user, Event event) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        if (eventService.findOne(event.getId()) == null) {
            throw new NonExistingEventException("Eventul cautat nu exista!");
        }

        eventService.unsbscribeToEvent(user, event);
    }

    public Friendship getFriendshipBetweenTwoUsers(User user, String username) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        try {
            Long friendsId = userService.getUserIdByUsername(username);
            userService.findOne(friendsId);

            Friendship friendship = null;

            Friendship firstCandidate = friendshipService.findOne(new Tuple<>(user.getId(), friendsId));
            if (firstCandidate != null) {
                friendship = firstCandidate;
            }

            Friendship secondCandidate = friendshipService.findOne(new Tuple<>(friendsId, user.getId()));
            if (secondCandidate != null) {
                friendship = secondCandidate;
            }

            return friendship;
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }
    }

    public Iterable<Event> getAllEvents() {
        return eventService.findAll();
    }

    public Event saveEvent(User creator, String name, String description, String location,
                           LocalDateTime date) {
        if (creator == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(creator.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        Event event = new Event(creator, name, description, location, date);

        return eventService.save(event);
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

    public boolean verifyIfUserIsCurrentUser(User user) {
        return user.getId().equals(getCurrentUser().getId());
    }

    public Message sendMessageToUsersFromCurrentUser(String message, List<String> names) {
        try {
            Message returnValue = sendMessageToUsers(getCurrentUser(), message, names);
            notifyObservers();

            return returnValue;
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public Message replyToMessage(User user1, String firstName, String lastName, String stringMessage, Long messageId) {
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

        return messageService.save(replyMessage);
    }

    public List<User> getAllUsersForConversations(User user) {
        return messageService.getAllUsersIdsForConversations(user).stream().map(id -> userService.findOne(id)).collect(Collectors.toList());
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

    public Message currentUserReply(String firstMName, String lastName, String stringMessage, Long messageId) {
        try {
            Message returnValue = replyToMessage(getCurrentUser(), firstMName, lastName, stringMessage, messageId);
            notifyObservers();

            return returnValue;
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent2 nu exista!");
        }
    }

    public void currentUserReplyToAll(String firstMName, String lastName, String stringMessage, Long messageId) {
        try {
            replyToAll(getCurrentUser(), firstMName, lastName, stringMessage, messageId);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public Message sendMessageToUsers(User user, String message, List<String> names) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        ArrayList<User> receivers = userService.getUsersByStringArray(names);
        return messageService.sendMessageToUsers(user, message, receivers);
    }

    public ArrayList<Message> conversationOfTwoUsers(User user1, String username) {
        if (user1 == null) {
            throw new IllegalArgumentException("Userul nu poate sa fie null!");
        }

        Long user2Id = userService.getUserIdByUsername(username);

        if (userService.findOne(user1.getId()) == null ||
                user2Id == null) {
            throw new NonExistingUserException("Utilizatorul cautat nu exista!");
        }

        return messageService.getConversationBetween2Users(user1, userService.findOne(user2Id));
    }

    public Long getUserIdByName(String firstName, String lastName) {
        return userService.getUserIdByName(firstName, lastName);
    }

    public ArrayList<Message> currentUsersConversationWithAnotherUser(String username) {
        try {
            return conversationOfTwoUsers(getCurrentUser(), username);
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

    public void deleteFriend(User user, String username) {
        if (user == null) {
            throw new IllegalArgumentException("Utilizatorul nu poate sa fie null!");
        }

        if (userService.getUserIdByUsername(user.getUsername()) == null) {
            throw new NonExistingUserException("Utilizatorul nu exista!");
        }

        Long friendId = userService.getUserIdByUsername(username);
        if (friendId == null) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }

        int numberOfFriends = numberOfFriends(user.getId());
        friendshipService.deleteFriendship(user.getId(), friendId);

        int newNumberOfFriends = numberOfFriends(user.getId());

        if (numberOfFriends == newNumberOfFriends) {
            throw new NonexistingFriendException("Prietenul cautat nu exista!");
        }
    }

    public void deleteFriendRequest(User user, String username) {
        if (user == null) {
            throw new IllegalArgumentException("Utilizatorul nu poate sa fie null!");
        }

        if (userService.getUserIdByUsername(user.getUsername()) == null) {
            throw new NonExistingUserException("Utilizatorul nu exista!");
        }

        Long friendId = userService.getUserIdByUsername(username);
        if (friendId == null) {
            throw new NonExistingUserException("Prietenul cautat nu exista!");
        }

        int numberOfFriends = numberOfFriends(user.getId());
        friendshipService.deleteFriendRequest(user.getId(), friendId);

        int newNumberOfFriends = numberOfFriends(user.getId());

        if (numberOfFriends == newNumberOfFriends) {
            throw new NonExistingFriendRequest("Cererea cautata nu exista!");
        }
    }

    public void deleteFriendRequestFromCurrentUser(String username) {
        try {
            deleteFriendRequest(getCurrentUser(), username);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Utilizatorul curent nu exista!");
        }
    }

    public void deleteFriendFromCurrentUser(String username) {
        try {
            deleteFriend(getCurrentUser(), username);
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

    public Friendship sendFriendRequest(User user, String username) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate fi nul!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Nu exista userul!");
        }

        try {
            Long friendsId = userService.getUserIdByUsername(username);
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

    public Friendship changeFriendshipStatus(User user, String username, String status) {
        if (user == null) {
            throw new IllegalArgumentException("Userul nu poate fi nul!");
        }

        if (userService.findOne(user.getId()) == null) {
            throw new NonExistingUserException("Nu exista userul!");
        }

        try {
            Long friendsId = userService.getUserIdByUsername(username);
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

    public Friendship approveFriendRequest(User user, String username) {
        Friendship f = changeFriendshipStatus(user, username, "approved");
        notifyObservers();

        return f;
    }

    public Friendship rejecteFriendRequest(User user, String username) {
        return changeFriendshipStatus(user, username, "rejected");
    }

    public Friendship rejectFriendRequestFromCurrentUser(String username) {
        try {
            return rejecteFriendRequest(getCurrentUser(), username);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Userul curent nu exista!");
        }
    }

    public Friendship approveFriendRequestFromCurrentUser(String username) {
        try {
            return approveFriendRequest(getCurrentUser(), username);
        } catch (IllegalArgumentException e) {
            throw new NonExistingUserException("Userul curent nu exista!");
        }
    }

    public Friendship sendFriendRequestFromCurrentUser(String username) {
        try {
            return sendFriendRequest(getCurrentUser(), username);
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
            String username;

            if (x.getId().getLeft().equals(user.getId())) {
                User friend = userService.findOne(x.getId().getRight());

                firstname = friend.getFirstName();
                lastname = friend.getLastName();
                username = friend.getUsername();
            } else {
                User friend = userService.findOne(x.getId().getLeft());

                firstname = friend.getFirstName();
                lastname = friend.getLastName();
                username = friend.getUsername();
            }
            return new FriendshipDTO(firstname, lastname, username, x.getDate());
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

    public Iterable<Message> getAllMessages() {
        return messageService.findAll();
    }

    public List<ReportItem> getAllFriendshipsAndReceivedMessagesByCurrentUserBetweenTwoDates(LocalDateTime date1, LocalDateTime date2) {
        List<ReportItem> reportItems = new ArrayList<>();

        getAllFriendshipsFromCurrentUserBetweenTwoDates(date1, date2).forEach(friendshipDTO -> {
            String content = "V-ati imprietenit cu " + friendshipDTO.getFriendFirstName() + " " + friendshipDTO.getFriendLastName() +
                    " la data de " + friendshipDTO.getDate().getDayOfMonth() + " " + friendshipDTO.getDate().getMonth().getValue() + " " +
                    friendshipDTO.getDate().getYear() + "!";
            ReportItem reportItem = new ReportItem(content, friendshipDTO.getDate());

            reportItems.add(reportItem);
        });

        getAllReceivedMessagesByCurrentUserBetweenTwoDates(date1, date2).forEach(message -> {
            String content = "Ati primit un mesajul " + message.getMessage() + " de la " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +
                    " la data de " + message.getDate().getDayOfMonth() + " " + message.getDate().getMonth().getValue() + " " +
                    message.getDate().getYear() + "!";
            ReportItem reportItem = new ReportItem(content, message.getDate());

            reportItems.add(reportItem);
        });

        return reportItems.stream().sorted(Comparator.comparing(ReportItem::getDate)).collect(Collectors.toList());
    }

    public List<Message> getAllMessagesReceivedByCurrentUserFromAnotherUserBetweenTwoDates(String username, LocalDateTime date1, LocalDateTime date2) {
        if (username == null) {
            throw new IllegalArgumentException("Userulname-ul nu poate fi nul!");
        }

        User user = getUserByUsername(username);
        if (user == null) {
            throw new NonExistingUserException("Nu exista userul!");
        }

        Friendship friendships1 = friendshipService.findOne(new Tuple<>(user.getId(), getCurrentUser().getId()));
        Friendship friendships2 = friendshipService.findOne(new Tuple<>(getCurrentUser().getId(), user.getId()));

        if (friendships1 != null) {
            if (!friendships1.getStatus().equals("approved")) {
                throw new NonexistingFriendException("Cei 2 nu sunt prieteni!");
            } else {
                return StreamSupport.stream(getAllMessages().spliterator(), false)
                        .filter(message -> message.getFrom().getId().equals(user.getId()) &&
                             message.getTo().contains(getCurrentUser()) &&
                             message.getDate().isAfter(date1) &&
                             message.getDate().isBefore(date2)).collect(Collectors.toList());
            }
        } else if(friendships2 != null) {
            if (!friendships2.getStatus().equals("approved")) {
                throw new NonexistingFriendException("Cei 2 nu sunt prieteni!");
            } else {
                return StreamSupport.stream(getAllMessages().spliterator(), false)
                        .filter(message -> message.getFrom().getId().equals(user.getId()) &&
                                message.getTo().contains(getCurrentUser()) &&
                                message.getDate().isAfter(date1) &&
                                message.getDate().isBefore(date2)).collect(Collectors.toList());
            }
        }

        throw new NonexistingFriendException("Cei 2 nu sunt prieteni!");
    }

    public List<Message> getAllReceivedMessagesByCurrentUserBetweenTwoDates(LocalDateTime date1, LocalDateTime date2) {
        return StreamSupport.stream(getAllMessages().spliterator(), false).filter(message -> message.getTo().contains(getCurrentUser()) && message.getDate().isAfter(date1) &&
                message.getDate().isBefore(date2)).collect(Collectors.toList());
    }

    public List<FriendshipDTO> getAllFriendshipsFromCurrentUserBetweenTwoDates(LocalDateTime date1, LocalDateTime date2) {
        return getAllFriendshipsFromCurrentUser()
                .stream()
                .filter(friendshipDTO -> friendshipDTO.getDate().isAfter(date1) && friendshipDTO.getDate().isBefore(date2)).collect(Collectors.toList());
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

        return friendships.stream().filter(x -> x.getId().getRight().equals(user.getId())
                && x.getStatus().equals("pending")
        ).map(x -> {
            String firstname;
            String lastname;
            String username;

            User friend = userService.findOne(x.getId().getLeft());

            firstname = friend.getFirstName();
            lastname = friend.getLastName();
            username = friend.getUsername();

            return new FriendRequestDTO(firstname, lastname, username, x.getDate(), x.getStatus());
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
