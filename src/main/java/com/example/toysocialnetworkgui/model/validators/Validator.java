package com.example.toysocialnetworkgui.model.validators;

/**
 * Interfata generica care implementeaza un validator pentru o entitate
 * @param <T> tipul entiatatii
 */
public interface Validator<T> {
    /**
     * Functia de validare a entitatii
     * @param entity - entitatea care terbuie validata
     */
    void validate(T entity);
}
