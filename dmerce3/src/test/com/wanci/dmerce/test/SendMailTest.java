/*
 * Datei angelegt am 20.11.2003
 */
package com.wanci.dmerce.test;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.wanci.dmerce.mail.SendMail;

/**
 * @author Masanori Fujita
 */
public class SendMailTest {
	
	public static void main(String[] args) throws AddressException, MessagingException {
		SendMail sm = new SendMail("mail.fujita.de");
		sm.setToHeader("masanori@fujita.de");
		sm.setFromHeader("masanori@fujita.de");
		sm.setSubject("[dmerce3@zeus] Kritischer Fehler");
		sm.sendMail();
	}

}
