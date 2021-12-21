package com.example.toysocialnetworkgui.repository.memory;

import com.example.toysocialnetworkgui.model.Entity;
import com.example.toysocialnetworkgui.model.validators.Validator;
import com.example.toysocialnetworkgui.repository.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository care pastreaza entitatile in memorie
 * Implementeaza interfata Repository
 * @param <ID> - tipul E trebuie sa aiba un atribut de tipul ID
 * @param <E> -  tipul entitatii salvate in repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private Validator<E> validator;
    private Map<ID, E> entities;

    /**
     *
     * Constructorul repository-ului
     * @param validator - validatorul entitatilor
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
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
    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        return entities.get(id);
    }

    /**
     *
     * @return toate entitatile
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     *
     * @param entity - entitatea care trebuie adaugata
     *         entity nu poate sa fie null
     * @return null- daca entitatea a fost salvata
     *         altfel returneaza entitatea (daca id-ul exista deja)
     * @throws IllegalArgumentException
     *             daca entitatea este null     *
     */
    @Override
    public E save(E entity) {
        if(entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        if (entities.get(entity.getId()) != null) {
            return entity;
        } else {
            entities.put(entity.getId(), entity);
        }

        return null;
    }

    /**
     *  sterge entitatea cu acest id
     * @param id
     *      id nu poate sa fie null
     * @return entitatea stersa sau null daca nu exista nicio entitatea cu acest id
     * @throws IllegalArgumentException
     *                   daca id-ul este null
     */
    @Override
    public E delete(ID id) {
        if(id == null) {
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        }

        E entity = entities.get(id);

        if (entity != null)
            entities.remove(id);

        return entity;
    }

    /**
     *
     * @param entity
     *          entity nu poate sa fie null
     * @return null - daca entitatea este modificata
     *                altfel returneaza entitatea  - (daca id-ul ei nu exista)
     * @throws IllegalArgumentException
     *             daca entity este null
     */
    @Override
    public E update(E entity) {
        if(entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate sa fie null!");
        }

        validator.validate(entity);

        if (entities.get(entity.getId()) != null) {
            entities.put(entity.getId(), entity);

            return null;
        }

        return entity;
    }
}
