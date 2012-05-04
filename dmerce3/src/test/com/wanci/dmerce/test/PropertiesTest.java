/*
 * Properties.java
 *
 * Created on October 1, 2003, 4:42 PM
 */

package com.wanci.dmerce.test;

import java.util.Properties;

/**
 *
 * @author  rb
 */
public class PropertiesTest {
    
    /** Creates a new instance of Properties */
    public PropertiesTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Properties p = System.getProperties();
        System.out.println(p.getProperty("test"));
        
    }
    
}