/*
 * Created on Apr 24, 2003
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import sun.net.TelnetInputStream;
import sun.net.ftp.FtpClient;

/**
 * @author mm
 * @version $Id: GetFilesUrlConnect.java,v 1.1 2004/03/29 13:39:32 rb Exp $
 */
public class GetFilesUrlConnect {

	private boolean DEBUG = false;

	private String filename;

	/**
	 * Liste wiedergeben lassen ueber FTP Verbindung
	 */
	public void getServerContent(GetFilesXmlParserInfo l) {

		try {
			FtpClient ftpClient;
			System.out.println("Connecting to " + l.getHost());
			ftpClient = new FtpClient(l.getHost(), 21);
			try {
				ftpClient.login(l.getUsername(), l.getPassword());
			}
			catch (Exception e) {
				System.out.println("Login fehlgeschlagen. " + e);
			}

			ftpClient.binary();
			try {
				ftpClient.cd(l.getPath());
			}
			catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
			}

			System.out.println("File oeffnen!");
			TelnetInputStream listing = ftpClient.get(l.getFilename());
			String[] server = new String[0];
			int c;
			int j = 0;
			while ((c = listing.read()) != -1) {
				server[j] = (Integer.toString(c));
				System.out.print(j);
				j++;
			}
			System.out.println(server);
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}

	/**
	 * Download einer Datei entweder ueber FTP oder HTTP;
	 * URL-typische Adresse angeben.
	 * funktioniert noch nicht!
	 */
	public void download(GetFilesXmlParserInfo l) {

		try {
			URL u = new URL(l.getUrl());
			System.out.println(l.getUrl());
			int real = l.getUrl().lastIndexOf("/");
			if (real != -1) {
				filename = l.getUrl().substring(real + 1);
			}
			//URLConnection connection = u.openConnection();
			InputStream stream = u.openStream();
			//InputStream aStream = connection.getInputStream();
			BufferedInputStream in = new BufferedInputStream(stream);
			FileOutputStream file = new FileOutputStream(filename);
			BufferedOutputStream out = new BufferedOutputStream(file);
			System.out.println("Downloading file: " + filename);
			int i;
			while ((i = in.read()) != -1) {
				out.write(i);
			}
			out.flush();
			System.out.println("Downloaded file: " + filename);
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}
}