/*
 * SignalHandlerTest.java
 *
 * Created on September 18, 2003, 7:34 PM
 */

package com.wanci.dmerce.test;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 *
 * @author  rb
 */
public class SignalHandlerTest {
    
    /** Creates a new instance of SignalHandlerTest */
    public SignalHandlerTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Aaarghh!");
            }
        });
        
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
            System.out.print(".");
        }
        
    }
    
}