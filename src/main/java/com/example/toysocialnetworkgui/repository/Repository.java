package com.example.toysocialnetworkgui.repository;

import com.example.toysocialnetworkgui.model.Entity;

/**
 * Interfata repository cu operatii CRUD
 * @param <ID> - tipul E trebuie sa aiba un atribut de tipul ID
 * @param <E> -  tipul entitatii salvate in repository
 */

public interface Repository<ID, E extends Entity<ID>> {
    /**
     *
     * @param id - id-ul entitatii care trebuie returnata
     *           id-ul nu poate fi null
     * @return entitatea cu id-ul specificat
     *          sau null - daca nu exista nicio entitate cu acest id
     * @throws IllegalArgumentException
     *                  daca id-ul este null
     */
    E findOne(ID id);

    /**
     *
     * @return toate entitatile
     */
    Iterable<E> findAll();

    /**
     *
     * @param entity - entitatea care trebuie adaugata
     *         entity nu poate sa fie null
     * @return null- daca entitatea a fost salvata
     *         altfel returneaza entitatea (daca id-ul exista deja)
     * @throws IllegalArgumentException
     *             daca entitatea este null     *
     */
    E save(E entity);

    /**
     *  sterge entitatea cu acest id
     * @param id
     *      id nu poate sa fie null
     * @return entitatea stersa sau null daca nu exista nicio entitatea cu acest id
     * @throws IllegalArgumentException
     *                   daca id-ul este null
     */
    E delete(ID id);

    /**
     *
     * @param entity
     *          entity nu poate sa fie null
     * @return null - daca entitatea este modificata
     *                altfel returneaza entitatea  - (daca id-ul ei nu exista)
     * @throws IllegalArgumentException
     *             daca entity este null
     */
    E update(E entity);
}
