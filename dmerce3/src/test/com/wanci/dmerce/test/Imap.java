/*
 * Created on Jul 7, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.test;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * @author rb
 * @version $Id: Imap.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 *
 * Test fuer das JavaMail-Interface: Anzeigen einer IMAP-Mailbox
 * 
 */
public class Imap {

    private Session session;
    private Store store = null;
    private Folder rf = null;
    private boolean recursive = true;
    //URLName urlName = new URLName("imap://rb:blabla@62.72.64.206/");

    public Imap() {

        System.setProperty("mail.host", "flower");
        System.setProperty("mail.smtp.host", "flower");
        Properties props = System.getProperties();

        session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

    }

    public void connectToImap() {
        //store = session.getStore(urlName);
        try {
            store = session.getStore("imap");
            store.connect("flower", "rb", "");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void dumpFolder(Folder folder, boolean recurse, String tab)
        throws Exception {
        System.out.println(tab + "Name: " + folder.getName());
        System.out.println(tab + "Full Name: " + folder.getFullName());
        System.out.println(tab + "URL: " + folder.getURLName());
        boolean verbose = true;
        if (verbose) {
            if (!folder.isSubscribed())
                System.out.println(tab + "Not Subscribed");
            if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
                if (folder.hasNewMessages())
                    System.out.println(tab + "Has New Messages");
                System.out.println(
                    tab + "Total Messages: " + folder.getMessageCount());
                System.out.println(
                    tab + "New Messages: " + folder.getNewMessageCount());
                System.out.println(
                    tab + "Unread Messages: " + folder.getUnreadMessageCount());
            }
            if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0)
                System.out.println(tab + "Is Directory");
        }
        System.out.println();
        if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
            if (recurse) {
                Folder[] f = folder.list();
                for (int i = 0; i < f.length; i++)
                    dumpFolder(f[i], recurse, tab + " ");
            }
        }
    }

    public void listAll(String root) {
        try {
            Folder folder = store.getDefaultFolder();
            if (folder == null) {
                System.out.println("No default folder");
                System.exit(1);
            }

            folder = folder.getFolder(root);
            if (folder == null) {
                System.out.println("Invalid folder");
                System.exit(1);
            }

            // try to open read/write and if that fails try read-only
            try {
                folder.open(Folder.READ_WRITE);
            } catch (MessagingException ex) {
                folder.open(Folder.READ_ONLY);
            }

            int totalMessages = folder.getMessageCount();
            int newMessages = folder.getNewMessageCount();
            System.out.println(
                "TOTAL=" + totalMessages + " NEW=" + newMessages);

            // List namespace
            if (root != null)
                rf = store.getFolder(root);
            else
                rf = store.getDefaultFolder();
            dumpFolder(rf, false, "");
            if ((rf.getType() & Folder.HOLDS_FOLDERS) != 0) {
                Folder[] f = rf.list("");
                for (int i = 0; i < f.length; i++)
                    dumpFolder(f[i], recursive, " ");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Imap s = new Imap();
        s.connectToImap();
        s.listAll("INBOX");

    }
    
}