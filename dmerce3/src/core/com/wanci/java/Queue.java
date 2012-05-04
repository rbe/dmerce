package com.wanci.java;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Das Interface der Queue-Collection.
 */
public interface Queue {
    
    /**
     * Fügt das Element o am Ende der Queue an. Falls
     * keine Ausnahme ausgelöst wurde, gibt die Methode
     * true zurück.
     *
     * @param o Ein Element/beliebiges Object
     */
    boolean add(Object o);
    
    /**
     * Liefert das erste Element der Queue und entfernt es
     * daraus. Falls die Queue leer ist, wird eine Ausnahme
     * des Typs NoSuchElementException ausgelöst.
     *
     * @exception NoSuchElementException
     */
    Object retrieve() throws NoSuchElementException;
    
    /**
     * Liefert die Anzahl der Elemente der Queue.
     */
    int size();
    
    /**
     * Entfernt alle Elemente aus der Queue.
     */
    void clear();
    
    /**
     * Liefert einen Iterator über alle Elemente der Queue.
     */
    Iterator iterator();
}