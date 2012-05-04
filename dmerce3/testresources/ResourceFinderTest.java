/*
 * ResourceFinderTest.java
 *
 * Created on September 1, 2003, 11:08 AM
 */

package test;

import java.net.URL;

/**
 *
 * @author  Masanori Fujita
 */
public class ResourceFinderTest {
    
    /** Creates a new instance of ResourceFinderTest */
    public ResourceFinderTest() {
    	
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ResourceFinderTest o = new ResourceFinderTest();
        URL url = o.getClass().getResource("../build-core.xmlg");
        System.out.println(url);
    }
    
}
