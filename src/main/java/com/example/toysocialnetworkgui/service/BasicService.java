package com.example.toysocialnetworkgui.service;

import com.example.toysocialnetworkgui.model.Entity;
import com.example.toysocialnetworkgui.repository.Repository;

/**
 * Clasa abstracta care modeleaza service-ul pentru un tip de entitate
 * @param <ID> - tipul E trebuie sa aiba un atribut de tipul ID
 * @param <E> -  tipul entitatii salvate in repository
 */
public abstract class BasicService<ID, E extends Entity<ID>>{
    protected Repository<ID, E> repository;

    /**
     * Contructorul service-ului
     * @param repository - repository-ul pentru care lucreaza service-ul
     */
    public BasicService(Repository<ID, E> repository) {
        this.repository = repository;
    }

    /**
     *
     * @param id - id-ul entitatii care trebuie returnata
     *           id-ul nu poate fi null
     * @return entitatea cu id-ul specificat
     *          sau null - daca nu exista nicio entitate cu acest id
     * @throws IllegalArgumentException
     *                  daca id-ul este null
     */
    public E findOne(ID id) {
        return repository.findOne(id);
    }

    /**
     *
     * @return toate entitatile
     */
    public Iterable<E> findAll() {
        return repository.findAll();
    }
}