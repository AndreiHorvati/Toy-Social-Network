package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.model.Friendship;
import com.example.toysocialnetworkgui.model.Tuple;
import com.example.toysocialnetworkgui.repository.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service-ul care se ocupa de entitatea FriendShip
 * Extinde clasa abstracta BasicService
 */
public class FriendshipService extends BasicService<Tuple<Long, Long>, Friendship> {

    /**
     * Contructorul service-ului
     *
     * @param repository - repository-ul pentru care lucreaza service-ul
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> repository) {
        super(repository);
    }

    /**
     * Salveaza o prietenie pe baza id-urilor sale
     *
     * @param id1 - id-ul primului prieten
     * @param id2 - id-ul celui de al doilea prueten
     * @return ce returneaza functia save din repository
     * @throws IllegalArgumentException daca id1 sau id2 sunt null
     */
    public Friendship save(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("Id-urile nu pot fi null!");
        }

        Friendship friendship = new Friendship();
        friendship.setId(new Tuple(id1, id2));

        return repository.save(friendship);
    }

    /**
     * Sterge o prietenie baza pe 2 id-uri
     *
     * @param id1 - id-ul primului utilizator
     * @param id2 - id-ul celui de al doilea utilizator
     */
    public void deleteFriendship(Long id1, Long id2) {
        Friendship firstCandidate = findOne(new Tuple<>(id1, id2));

        if (firstCandidate != null && firstCandidate.getStatus().equals("approved")) {
            repository.delete(new Tuple<>(id1, id2));
        }

        Friendship secondCandidate = findOne(new Tuple<>(id2, id1));

        if (secondCandidate != null && secondCandidate.getStatus().equals("approved")) {
            repository.delete(new Tuple<>(id2, id1));
        }
    }

    public void deleteFriendRequest(Long id1, Long id2) {
        Friendship friendship = findOne(new Tuple<>(id1, id2));

        if (friendship != null && friendship.getStatus().equals("pending")) {
            repository.delete(new Tuple<>(id1, id2));
        }
    }

    /**
     * Sterge toate priteniile unui user pe baza id-ului sau
     *
     * @param id - id-ul userului pentru care stergem prieteniile
     * @throws IllegalArgumentException daca id-ul este null
     */
    public void deleteFriends(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        List<Tuple<Long, Long>> idsToRemove = new ArrayList<>();

        for (Friendship friendship : findAll()) {
            if (friendship.getId().getLeft().equals(id) || friendship.getId().getRight().equals(id)) {
                idsToRemove.add(friendship.getId());
            }
        }

        for (Tuple<Long, Long> idToRemove : idsToRemove) {
            repository.delete(idToRemove);
        }
    }

    public Friendship updateFriend(Friendship friendship) {
        return repository.update(friendship);
    }
}
