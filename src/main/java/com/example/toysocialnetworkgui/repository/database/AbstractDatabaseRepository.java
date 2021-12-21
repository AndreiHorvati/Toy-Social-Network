package com.example.toysocialnetworkgui.repository.database;

import com.example.toysocialnetworkgui.model.Entity;
import com.example.toysocialnetworkgui.model.validators.Validator;
import com.example.toysocialnetworkgui.repository.Repository;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected String url;
    protected String username;
    protected String password;
    protected Validator<E> validator;

    public AbstractDatabaseRepository(String url, String username, String password, Validator<E> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public abstract E findOne(ID id);

    @Override
    public abstract Iterable<E> findAll();

    @Override
    public abstract E save(E entity);

    @Override
    public abstract E delete(ID id);

    @Override
    public abstract E update(E entity);
}
