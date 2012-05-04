/*
 * Created on Nov 17, 2003
 *
 */
package com.wanci.customer.callando;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: FtpTransfer.java,v 1.2 2004/07/16 13:36:31 rb Exp $
 *  
 */
public class FtpTransfer {

	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private Realm realm;

	private String ftpHost;

	private String ftpPwd;

	private String ftpUser;

	/**
	 * 
	 * @param ftpHost
	 * @param ftpUser
	 * @param ftpPwd
	 */
	public FtpTransfer(Realm realm, String ftpHost, String ftpUser,
			String ftpPwd) {
		this.realm = realm;
		this.ftpHost = ftpHost;
		this.ftpUser = ftpUser;
		this.ftpPwd = ftpPwd;
	}

	public String getDateAndNumberForRetrieval() {

		String s = "00";
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);

		if (hour < 14)
			s = "01";
		else if (hour >= 14 && hour < 18)
			s = "02";
		else if (hour >= 18)
			s = "03";

		return new SimpleDateFormat("yyyyMMdd").format(c.getTime()) + "-" + s;

	}

	/**
	 * Returns 1, 2 or 3 depending on the actual hour
	 * 
	 * 1 = 0:00 - 12:00 2 = 13:00 - 16:00 3 = 17:00 - 23:00
	 *  
	 */
	public String getDateAndNumberForTransmission() {

		String s = "00";
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);

		if (hour < 10)
			s = "01";
		else if (hour >= 10 && hour < 14)
			s = "02";
		else if (hour >= 14)
			s = "03";

		return new SimpleDateFormat("yyyyMMdd").format(c.getTime()) + "-" + s;

	}

	/**
	 * 
	 * @param filename
	 * @return @throws
	 *         IOException
	 * @throws FTPException
	 */
	public ByteArrayInputStream getFile(String filename) throws IOException,
			FTPException {

		FTPClient ftp = new FTPClient(ftpHost, 21);
		ftp.debugResponses(true);
		ftp.login(ftpUser, ftpPwd);
		ftp.setType(FTPTransferType.ASCII);
		ftp.setConnectMode(FTPConnectMode.PASV);

		byte[] buf = ftp.get(filename);
		LangUtil.consoleDebug(DEBUG, "Got " + buf.length + " bytes");

		ftp.quit();

		return new ByteArrayInputStream(buf);

	}

	/**
	 * Retrieve file mw-YYYYMMDD-0[1|2|3] from FTP server of mediaWays and
	 * return a ByteArrayInputStream containing transfered data
	 */
	public ByteArrayInputStream getRecentFile() throws IOException,
			FTPException {

		FTPClient ftp = new FTPClient(ftpHost, 21);
		ftp.debugResponses(true);
		ftp.login(ftpUser, ftpPwd);
		ftp.setType(FTPTransferType.ASCII);
		ftp.setConnectMode(FTPConnectMode.PASV);

		String s = "mw-" + getDateAndNumberForRetrieval();
		//System.out.println(s);
		byte[] buf = ftp.get(s);
		LangUtil.consoleDebug(DEBUG, "Got " + buf.length + " bytes");

		ftp.quit();

		return new ByteArrayInputStream(buf);

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 * @throws FTPException
	 */
	public void putFile(InputStream file) throws IOException, FTPException {

		FTPClient ftp = new FTPClient(ftpHost, 21);
		ftp.debugResponses(true);
		ftp.login(ftpUser, ftpPwd);
		ftp.setType(FTPTransferType.ASCII);
		ftp.setConnectMode(FTPConnectMode.PASV);

		//ftp.put(file, "callandodsl-" + getDateAndNumberForTransmission());
		ftp.put(file, realm.getRealmPrefix() + "-"
				+ getDateAndNumberForTransmission());

		ftp.quit();

	}
}