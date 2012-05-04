/*
 * GenerateMediaWaysFile.java
 *
 * Created on September 18, 2003, 6:01 PM
 */

package com.wanci.customer.callando;

import java.io.FileWriter;
import java.util.Iterator;

/**
 *
 * @author  rb
 */
public class GenerateMediaWaysFile {
    
    private AccountPool accountPool;
    
    private FileWriter fileWriter;
    
    private StringBuffer fileContent = new StringBuffer();
    
    /** Creates a new instance of ProcessRequests */
    public GenerateMediaWaysFile(AccountPool accountPool) {
        this.accountPool = accountPool;
    }
    
    public String getFileContent() {
        return fileContent.toString();
    }
    
    /**
     * Walk through all accounts and record an action for an account
     * in buffer 'fileContent'
     */
    public void processAllAccounts() {
        
        Iterator accountPoolIterator = accountPool.getAccountIterator();
        while (accountPoolIterator.hasNext()) {
            
            Account a = (Account) accountPoolIterator.next();
            String s = a.getAction();
            if (s != null)
                fileContent.append(s + "\n");
            
        }
        
    }
    
}