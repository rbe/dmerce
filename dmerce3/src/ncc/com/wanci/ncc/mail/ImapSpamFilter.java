/*
 * Created on 29.01.2004
 *  
 */
package com.wanci.ncc.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * @author rb
 * @version ${Id}
 *  
 */
public class ImapSpamFilter {

	private Session session;
	private Store store = null;

	/**
	 * Constructor
	 *  
	 */
	public ImapSpamFilter(URLName urlName) throws MessagingException {

		System.setProperty("mail.host", "bigspacer");
		Properties props = System.getProperties();

		session = Session.getDefaultInstance(props, null);
		session.setDebug(false);

		store = session.getStore(urlName);
		store.connect();

	}

	/**
	 * @param is
	 * @param fileName
	 * @throws IOException
	 */
	void saveAttachment(InputStream is, String fileName) throws IOException {

		// Create a file with the same name as the attachment
		File file = new File(fileName);

		// Open an output stream on the file
		FileOutputStream fos = new FileOutputStream(file);

		// Write each byte from the input stream to the file
		int j;
		while ((j = is.read()) != -1)
			fos.write(j);

		// Close the file for output
		fos.close();

	}

	/**
	 * @param message
	 * @throws MessagingException
	 */
	void setUnreadFlag(Message message) throws MessagingException {
		message.setFlag(Flags.Flag.SEEN, false);
	}

	/**
	 * @param message
	 * @throws MessagingException
	 */
	void setDeletedFlag(Message message) throws MessagingException {
		message.setFlag(Flags.Flag.DELETED, true);
	}

	/**
	 * @param folderName
	 * @throws IOException
	 * @throws MessagingException
	 */
	void scanFolder(String folderName) throws IOException, MessagingException {

		Folder folder = store.getFolder(folderName);
		folder.open(Folder.READ_WRITE);
		System.out.println(
			"Processing folder: "
				+ folder.getName()
				+ " with "
				+ folder.getMessageCount()
				+ " messages");

		Message[] msgs = folder.getMessages();

		FetchProfile fp = new FetchProfile();
		fp.add(FetchProfile.Item.ENVELOPE);
		fp.add(FetchProfile.Item.FLAGS);
		folder.fetch(msgs, fp);

		for (int i = 0; i < msgs.length; i++) {

			Message message = msgs[i];
			Flags flags = message.getFlags();

			if (!flags.contains(Flags.Flag.DELETED)) {

				String from = message.getFrom()[0].toString();
				String replyTo = message.getReplyTo()[0].toString();
				String subject = message.getSubject();
				String contentType = message.getContentType();
				System.out.println(subject);

				if (message.isMimeType("multipart/*")) {

					BodyPart part = null;
					Multipart multipart = (Multipart) message.getContent();

					try {

						for (int m = 0; m < multipart.getCount(); m++) {

							part = multipart.getBodyPart(m);
							Object o = (Object) part.getContent();
							if (o instanceof InputStream) {

								// Retrieve the file name
								String fileName = part.getFileName();

								if (fileName != null) {

									if (fileName.indexOf(".pdf") >= 0) {
										System.out.println(
											"Found attachment .pdf");
										setDeletedFlag(message);
									}

									/*
									 * saveAttachment( part.getInputStream(),
									 * "RB-" + part.getFileName());
									 */

								}

							}

						}

						setUnreadFlag(message);

					}
					catch (MessagingException me) {
						System.out.println("2: " + me.getMessage());
					}

				}

			}

		}

		folder.close(false);
		store.close();

	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws MessagingException
	 */
	public static void main(String[] args)
		throws IOException, MessagingException {
		
		/*
		SpamFilterRule i = new ImaSpamFilterRule();
		i.addSearchTerm(new SubjectTerm());
		*/

		ImapSpamFilter s =
			new ImapSpamFilter(new URLName("imap://rb:bla@bigspacer/"));
		s.scanFolder("INBOX");

	}

}