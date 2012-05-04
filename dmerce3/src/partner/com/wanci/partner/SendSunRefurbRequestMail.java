/*
 * Created on Feb 24, 2003
 *
 */
package com.wanci.partner;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author rb
 * @version $Id: SendSunRefurbRequestMail.java,v 1.1 2003/12/03 19:58:32 rb Exp $
 *
 * Sendet eine Mail an die Sun-Refurb-Liste und verarbeitet
 * die ankommende Mail
 *
 */
public class SendSunRefurbRequestMail {

	/**
     * 
	 */
    private Session session;
	
    /**
     * 
     */
    //private Store store = null;
	
    /**
     * 
     */
    //private Folder rf = null;
	
    /**
     * 
     */
    //private boolean recursive = true;
	
    //URLName urlName = new URLName("imap://rb:blabla@62.72.64.206/");

	/**
     * 
	 */
    public SendSunRefurbRequestMail() {

		System.setProperty("mail.host", "flower");
		System.setProperty("mail.smtp.host", "flower");
		Properties props = System.getProperties();

		session = Session.getDefaultInstance(props, null);
		session.setDebug(true);

	}

	/**
     * 
	 * @param from
	 * @param to
	 */
    public void sendMail(String from, String to) {

		try {

			String msgText1 = "Text1";
			String msgText2 = "Text2";

			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to)};
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject("JavaMail APIs Multipart Test");
			msg.setSentDate(new Date());

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(msgText1);

			// create and fill the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();
            // Use setText(text, charset), to show it off!
			mbp2.setText(msgText2, "us-ascii");

			// create the Multipart and its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);

			// add the Multipart to the message
			msg.setContent(mp);

			// send the message
			Transport.send(msg);

		} catch (MessagingException mex) {

			mex.printStackTrace();
			Exception ex = null;

			if ((ex = mex.getNextException()) != null) {
				ex.printStackTrace();
			}

		}

	}

	/**
     * 
	 * @param args
	 */
    public static void main(String[] args) {

		SendSunRefurbRequestMail s = new SendSunRefurbRequestMail();
		s.sendMail("r.bensmann@1ci.de", "r.bensmann@1ci.de");

	}
    
}