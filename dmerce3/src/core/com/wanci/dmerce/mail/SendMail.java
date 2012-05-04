/*
 * Created on Oct 27, 2003
 *  
 */
package com.wanci.dmerce.mail;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: SendMail.java,v 1.8 2004/04/15 11:58:46 rb Exp $
 * 
 * Send mail from Java using JavaMail. You need activation.jar, mail.jar in
 * your classpath.
 * 
 * <pre>
 *  SendMail s = new SendMail(&quot;mailserver&quot;);
 *  s.setToHeader(&quot;rb@1ci.de&quot;);
 *  s.setFromHeader(&quot;ralf@bensmann.de&quot;);
 *  s.addBody(&quot;test1&quot;);
 *  s.sendMail();
 * </pre>
 * 
 * 
 *  
 */
public class SendMail {

	public final int TEXTPLAIN = 1;

	public final int TEXTHTML = 2;

	/**
	 *  
	 */
	private Session session;

	/**
	 *  
	 */
	private MimeMessage msg;

	/**
	 *  
	 */
	private Multipart mp;

	/**
	 * Email addresses for To:-header
	 */
	private Vector toHeader = new Vector();

	/**
	 * Email addresses for Cc:-header
	 */
	private Vector ccHeader = new Vector();

	/**
	 * Count for Cc:-header
	 */
	private int ccHeaderCount = 0;

	/**
	 * Was Cc:-header set?
	 */
	private boolean ccHeaderSet = false;

	/**
	 * Email addresses for Bcc:-header
	 */
	private Vector bccHeader = new Vector();

	/**
	 * Count for Bcc:-header
	 */
	private int bccHeaderCount = 0;

	/**
	 * Was Bcc:-header set?
	 */
	private boolean bccHeaderSet = false;

	/**
	 * Was a subject set? (setSubject()) If not, sendMail() will assign a
	 * subject "(no subject)"
	 */
	private boolean setSubjectCalled = false;

	private StringBuffer body = new StringBuffer();

	/**
	 * Default constructor. Uses "mailhost" for mail.host and mail.smtp.host
	 * System.setProperty(). A host called mailhost is used to send mail. If
	 * you don't have a such a host in your domain, try another constructor.
	 */
	public SendMail() {

		System.setProperty("mail.host", "mailhost");
		System.setProperty("mail.smtp.host", "mailhost");

		session = Session.getDefaultInstance(System.getProperties(), null);
		session.setDebug(false);

		msg = new MimeMessage(session);
		try {
			msg.setSubject("(no subject)");
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}

		mp = new MimeMultipart();

	}

	/**
	 * Constructor which takes one argument: a string for the mailserver to
	 * use.
	 * 
	 * @param mailserver
	 */
	public SendMail(String mailserver) {

		System.setProperty("mail.host", mailserver);
		System.setProperty("mail.smtp.host", mailserver);

		session = Session.getDefaultInstance(System.getProperties(), null);
		session.setDebug(false);

		msg = new MimeMessage(session);
		try {
			msg.setSubject("(no subject)");
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}

		mp = new MimeMultipart();

	}

	/**
	 * @param body
	 * @param contentType
	 * @throws MessagingException
	 */
	public void addBody(String body, int contentType) throws MessagingException {

		this.body.append(body + "\n");

		MimeBodyPart mbp = new MimeBodyPart();
		if (contentType == TEXTPLAIN)
			mbp.setContent(body, "text/plain");
		else if (contentType == TEXTHTML)
			mbp.setContent(body, "text/html");
		mp.addBodyPart(mbp);

	}

	/**
	 * @param value
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void addBccHeader(String value) throws AddressException,
			MessagingException {

		bccHeader.add(new InternetAddress(value));
		bccHeaderSet = true;

	}

	/**
	 * @param value
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void addCcHeader(String value) throws AddressException,
			MessagingException {

		ccHeader.add(new InternetAddress(value));
		ccHeaderSet = true;

	}

	/**
	 * Return From: header as string
	 * 
	 * @return
	 */
	public Address getFromHeader() throws MessagingException {
		return msg.getFrom()[0];
	}

	/**
	 * Return Subject: header as string
	 * 
	 * @return
	 */
	public String getSubject() throws MessagingException {
		return msg.getSubject();
	}

	public Iterator getToHeaderIterator() {
		return toHeader.iterator();
	}

	public Iterator getCcHeaderIterator() {
		return ccHeader.iterator();
	}

	/**
	 * Return To: header as string
	 * 
	 * @return
	 */
	public String getToHeaderString() {

		StringBuffer sb = new StringBuffer();

		Iterator it = getToHeaderIterator();
		while (it.hasNext()) {
			Address a = (Address) it.next();
			sb.append(a.toString());
		}

		return sb.toString();

	}

	/**
	 * Return Cc: header as string
	 * 
	 * @return
	 */
	public String getCcHeaderString() {

		StringBuffer sb = new StringBuffer();

		Iterator it = getCcHeaderIterator();
		while (it.hasNext()) {
			Address a = (Address) it.next();
			sb.append(a.toString());
		}

		return sb.toString();

	}

	/**
	 * Set debug mode in JavaMail-session (showing SMTP protocol log)
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		session.setDebug(debug);
	}

	/**
	 * @param value
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setFromHeader(String value) throws AddressException,
			MessagingException {

		msg.setFrom(new InternetAddress(value));

	}

	/**
	 * @param value
	 * @throws MessagingException
	 */
	public void setSubject(String value) throws MessagingException {
		msg.setSubject(value);
		setSubjectCalled = true;
	}

	/**
	 * @param header
	 * @param value
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setToHeader(String value) throws AddressException,
			MessagingException {

		toHeader.add(new InternetAddress(value));

	}

	/**
	 * Return body as string
	 * 
	 * @return
	 */
	public String getBody() {
		return body.toString();
	}

	/**
	 * Sets headers to defined values and add multipart to the message, then
	 * send it. If no subject was set using setSubject(), sendMail() will use
	 * "(no subject)"
	 * 
	 * @param from
	 * @param to
	 * @throws MessagingException
	 */
	public void sendMail() throws MessagingException {

		// Set headers
		msg.setRecipients(Message.RecipientType.TO, LangUtil
				.vectorToInternetAddressArray(toHeader));

		if (ccHeaderSet)
			msg.setRecipients(Message.RecipientType.CC, LangUtil
					.vectorToInternetAddressArray(ccHeader));

		if (bccHeaderSet)
			msg.setRecipients(Message.RecipientType.BCC, LangUtil
					.vectorToInternetAddressArray(bccHeader));

		msg.setSentDate(new Date());

		//if (!setSubjectCalled)
		//msg.setSubject("(no subject)");

		// Add the multipart to the message
		msg.setContent(mp);

		// Send the message
		Transport.send(msg);

	}

	/**
	 * Return mail in a readable format
	 */
	public String toString() {

		try {
			return "Subject: " + getSubject() + "\n" + "To: "
					+ getToHeaderString() + "\n" + "Cc: " + getCcHeaderString()
					+ "\n\n" + getBody();
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}

		return null;

	}

}