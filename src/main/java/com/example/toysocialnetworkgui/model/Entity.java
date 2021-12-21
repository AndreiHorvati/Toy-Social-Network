package com.example.toysocialnetworkgui.model;

import java.io.Serializable;

/**
 * Clasa generica care implementeaz o entitate avand un id
 * @param <ID> - entitatea trebuie sa aiba un atribut de tipul ID
 */
public class Entity<ID> implements Serializable {
    private ID id;

    /**
     * Getter id
     * @return id-ul entitatii
     */
    public ID getId() {
        return id;
    }

    /**
     * Seteaza un noi id entitatii
     * @param id noul id
     */
    public void setId(ID id) {
        this.id = id;
    }
}
