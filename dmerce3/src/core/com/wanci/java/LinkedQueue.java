package com.wanci.java;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Die folgende Klasse realisiert eine Queue-Collection
 * auf der Basis einer einfach verketteten linearen Liste.
 * Die LinkedQueue kann im Prinzip beliebig viele Elemente
 * aufnehmen. Die Laufzeiten der Einfüge- und Löschmethoden
 * sind O(1). Der von iterator() gelieferte Iterator
 * besitzt KEINE Implementierung der Methode remove().
 * 
 */
public class LinkedQueue implements Queue {
    
    protected ElementWrapper first;
    protected ElementWrapper last;
    protected int count;
    
    public LinkedQueue() {
        first = last = null;
        count = 0;
    }
    
    public boolean add(Object o) {
        if (count == 0) {
            //insert first element
            first = new ElementWrapper();
            last = first;
            count = 1;
        } else {
            //insert element into non-empty queue
            last.next = new ElementWrapper();
            last = last.next;
            ++count;
        }
        last.element = o;
        last.next = null;
        return true;
    }
    
    public Object retrieve() throws NoSuchElementException {
        if (count <= 0)
            throw new NoSuchElementException();
        ElementWrapper ret = first;
        --count;
        first = first.next;
        if (first == null) {
            last = null;
            count = 0;
        }
        return ret.element;
    }
    
    public int size() {
        return count;
    }
    
    public void clear() {
        while (first != null) {
            ElementWrapper tmp = first;
            first = first.next;
            tmp.next = null;
        }
        first = last = null;
        count = 0;
    }
    
    public Iterator iterator() {
        return new Iterator() {
            
            ElementWrapper tmp = first;
            
            public boolean hasNext() {
                return tmp != null;
            }
            
            public Object next() {
                if (tmp == null)
                    throw new NoSuchElementException();
                Object ret = tmp.element;
                tmp = tmp.next;
                return ret;
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
    }
    
    /** Lokale Wrapperklasse für die Queue-Elemente.
     */
    class ElementWrapper {
        
        public Object element;
        public ElementWrapper next;
        
    }
    
}