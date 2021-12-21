package com.example.toysocialnetworkgui.repository.file;

import com.example.toysocialnetworkgui.model.Entity;
import com.example.toysocialnetworkgui.model.validators.Validator;
import com.example.toysocialnetworkgui.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Repository care pastreaza entitatile in fisier
 * Extinde clasa InMemoryRepository
 * @param <ID> - tipul E trebuie sa aiba un atribut de tipul ID
 * @param <E> -  tipul entitatii salvate in repository
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String file;

    /**
     *
     * Constructorul repository-ului
     * @param validator - validatorul entitatilor
     * @param file - fisierul in care pastreaza entitatile
     */
    public AbstractFileRepository(Validator<E> validator, String file) {
        super(validator);
        this.file = file;

        loadFromFile();
    }

    @Override
    public E save(E entity) {
        E e = super.save(entity);

        if(e == null) {
            writeToFile(entity);
        }

        return e;
    }

    @Override
    public E update(E entity) {
        E e = super.update(entity);

        if(e == null) {
            writeAllToFile();
        }

        return e;
    }

    /**
     *  extract entity  - template method design pattern
     *  creaza o entitate de tipul E avand o lista de atribute
     * @param attributes - atributele entitatii
     * @return o entitate de tipul e
     */
    protected abstract E extractEntity(List<String> attributes);

    /**
     * creaza un string pe baza entitatii primite
     * @param entity - entitatea pentru care creaza stringul
     * @return stringul creat
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Incarca datele din fisier
     */
    private void loadFromFile() {
        String line;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while((line = bufferedReader.readLine()) != null) {
                List<String> attribues = Arrays.asList(line.split(","));
                super.save(extractEntity(attribues));
            }
        } catch (IOException e) {
            throw new FileException("Eroare la fisier!");
        }
    }

    /**
     * scrie o entitate in fisier facnd append
     * @param entity - entitatea scrisa
     */
    protected void writeToFile(E entity) {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            bufferedWriter.write(createEntityAsString(entity));
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new FileException("Eroare la fisier!");
        }
    }

    /**
     * goleste fisierul repository-ului
     */
    private void cleareFile() {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
            bufferedWriter.write("");
        } catch (IOException e) {
            throw new FileException("Eroare la fisier!");
        }
    }

    /**
     * Scrie in fisier toate entitatile existente
     */
    protected void writeAllToFile() {
        cleareFile();
        findAll().forEach(this::writeToFile);
    }

    @Override
    public E delete(ID id) {
        E entity = super.delete(id);

        if(entity != null) {
            writeAllToFile();
        }

        return entity;
    }
}
