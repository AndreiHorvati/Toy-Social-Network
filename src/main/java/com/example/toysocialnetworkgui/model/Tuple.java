package com.example.toysocialnetworkgui.model;

import java.util.Objects;

/**
 * Clasa care implementeaza un tuplu de marime 2
 */
public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    /**
     * Constructorul tuplului
     * @param e1 - primul element din tuplu
     * @param e2 - al doilea element din tuplu
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Getter pentru primul element din tuplu
     * @return primul element din tuplu
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * Getter pentru al doilea element din tuplu
     * @return al doilea element din tuplu
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * Verifica daca un tuplu este egal cu alt obiect
     * @param obj - obiectul cu care face comparatia
     * @return - true daca sunt egali altfel false
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof Tuple tuple)) {
            return false;
        }

        return getLeft().equals(tuple.getLeft()) &&
                getRight().equals(tuple.getRight());
    }

    /**
     * @return hash code-ul tuplului
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}
