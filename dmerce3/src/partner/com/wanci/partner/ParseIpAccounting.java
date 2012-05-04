/*
 * Created on Nov 5, 2003
 *  
 */
package com.wanci.partner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version ${Id}
 *  
 */
public class ParseIpAccounting {

	boolean DEBUG = false;

	/**
	 * Accounting per month per IP address (key = month)
	 */
	HashMap acctMap = new HashMap();

	Calendar c = Calendar.getInstance();

	Integer y = null;

	HashMap year = null;

	Integer m = null;

	HashMap month = null;

	/**
	 * Mapping between IP address spaces and provider and customer names
	 */
	String[][] replaces = new String[][] {
			{ "213.203.245.29", "BVK - dmerce.bvk.de" },
			{ "213.203.245.30", "BVK - www.bvk.de" },
			{ "213.203.245.70", "1Ci - frikkel-1.hamburg3-1" },
			{ "213.203.245.71", "1Ci - frikkel-2.hamburg3-1" },
			{ "213.203.245.72", "1BEL - www.datapage.de" },
			{ "213.203.245.73", "1BEL - www.winsoft.de" },
			{ "213.203.245.74", "1BEL - frikkel-1.hamburg3-1 - #74" },
			{ "213.203.245.75", "1BEL - frikkel-1.hamburg3-1 - #75" },
			{ "213.203.245.76", "MAQ - dmerce 3 - www.chubaka-theory.de" },
			{ "213.203.245.77", "MAQ - dmerce 3 - www.maq-design.de" },
			{ "213.203.245.78", "1Ci - docs.1ci.com" },
			{ "213.203.245.79", "1Ci - appl.1ci.com" },
			{ "213.203.245.90", "OBETA - www.obeta.de" },
			{ "213.203.245.91", "OBETA - www.winbox2000.de" },
			{ "213.203.245.92", "OBETA - www.teel.de" },
			{ "213.203.245.111", "1Ci - sid.hamburg3-1" },
			{ "213.203.245.112", "OBETA - mobile.obeta.de" },
			{ "213.203.245.123", "1Ci - www.1ci.de" },
			{ "213.203.245.124", "1Ci - Hosting, statisch/PHP" },
			{ "213.203.245.125", "1Ci - www.used-sun.de" },
			{ "213.203.245.126", "1Ci - Branchenloesungen" },
			{ "213.203.245.127", "MAQ - dmerce 3 - www.amigoh.de" },
			{ "213.203.245.128", "VW Club MS" }, { "213.203.245.129", "VSV" },
			{ "213.203.245.130", "IGGA - dmerce/www.igga.de" },
			{ "213.203.245.131", "AVV" }, { "213.203.245.132", "CAT" },
			{ "213.203.245.133", "Kompaktsound" },
			{ "213.203.245.134", "callando - 134" },
			{ "213.203.245.137", "compoint2000-Kunde" },
			{ "213.203.245.138", "callando - 138" },
			{ "213.203.245.139", "MAQ - dmerce 3 - www.hotel-bitzer.de" },
			{ "213.203.245.140", "OBETA - www.eldis-hh.de" },
			{ "213.203.245.141", "1Ci - webmail.1ci.com" },
			{ "213.203.245.142", "MAQ - dmerce 3 - www.gig-portal.de" },
			{ "213.203.245.142", "ITP - dmerce 3 - www.sports-one.info" },
			{ "213.203.245.", "HH - Level 3 - hamburg3 - " },
			{ "213.128.150.", "HH - IPHH - cini.hamburg2" },
			{ "213.128.138.", "HH - IPHH - ns1.hamburg2" } };

	/**
	 * Inner class that accounts bytes per ip address
	 * 
	 * @author rb
	 * @version ${Id}
	 *  
	 */
	class Acct {

		String ip;

		double bytesIn;

		double bytesOut;

		double bytesTotal;

		Acct(String ip, double bytesIn, double bytesOut, double bytesTotal) {

			this.ip = ip;
			this.bytesIn = bytesIn;
			this.bytesOut = bytesOut;
			this.bytesTotal = bytesTotal;

		}

		void addBytesIn(double addBytesIn) {
			this.bytesIn += addBytesIn;
		}

		void addBytesOut(double addBytesOut) {
			this.bytesOut += addBytesOut;
		}

		void addBytesTotal(double addBytesTotal) {
			this.bytesTotal += addBytesTotal;
		}

		double getBytesIn() {
			return bytesIn;
		}

		double getBytesOut() {
			return bytesOut;
		}

		double getBytesTotal() {
			return bytesTotal;
		}

		double getMegabytesTotal() {
			return bytesTotal / 1024 / 1024;
		}

		double getGigabytesTotal() {
			return bytesTotal / 1024 / 1024 / 1024;
		}

	}

	/**
	 * @param line
	 */
	void parseMailHeader(String line) throws ParseException {

		// Look for Date:-header
		if (line.startsWith("Date:")) {

			// Substring: only catch dd MMM yyyy
			SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy",
					new Locale("en_US"));
			c.setTimeInMillis(df.parse(line.substring(11, 22)).getTime());

			LangUtil.consoleDebug(DEBUG, "Mail date: " + c.get(Calendar.YEAR)
					+ (c.get(Calendar.MONTH) + 1));

			// Fetch year from hashmap to access months of a year
			y = new Integer(c.get(Calendar.YEAR));
			if (!acctMap.containsKey(y)) {
				LangUtil.consoleDebug(DEBUG, "Creating year " + y
						+ " in acctMap");
				acctMap.put(y, new HashMap());
			}

			year = (HashMap) acctMap.get(y);

			// Fetch month from hashmap to access ip accounting objects
			// per month/per ip
			m = new Integer(c.get(Calendar.MONTH) + 1);
			if (!year.containsKey(m)) {
				LangUtil.consoleDebug(DEBUG, "Creating month " + m
						+ " in year " + y);
				year.put(m, new HashMap());
			}

			month = (HashMap) year.get(m);

		}

	}

	/**
	 * @param file
	 * @throws IOException
	 */
	void parseMesh(File file) throws IOException, ParseException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		boolean startParsing = false;

		while ((line = br.readLine()) != null) {

			// Start parsing when mail header is passed
			// else parse mail header for Date and fetch needed
			// objects for year, month and ip accounting.
			if (line.length() == 1)
				startParsing = true;
			else if (line.length() > 1) {
				parseMailHeader(line);

				// Omit lines if we should not start parsing or
				// the line is commented out
				if (!startParsing || line.charAt(0) == '#')
					continue;

				if (year != null && month != null) {

					// Parse line with ip, bytes in, out and total
					//LangUtil.consoleDebug(DEBUG, ("Parsing line: " + line);
					StringTokenizer st = new StringTokenizer(line);
					String ip = st.nextToken();
					long bytesIn = new Long(st.nextToken()).longValue();
					long bytesOut = new Long(st.nextToken()).longValue();
					long bytesTotal = new Long(st.nextToken()).longValue();

					if (!month.containsKey(ip)) {
						LangUtil.consoleDebug(DEBUG,
								"Creating ip acct object for ip " + ip
										+ " in year " + y + "-" + m + ": " + ip
										+ " bytesTotal=" + bytesTotal);
						month.put(ip, new Acct(ip, bytesIn, bytesOut,
								bytesTotal));
					} else {

						LangUtil.consoleDebug(DEBUG,
								"Modifying ip acct object for ip " + ip
										+ " in year " + y + "-" + m + ": " + ip
										+ " bytesTotal=" + bytesTotal);
						Acct acct = (Acct) month.get(ip);
						acct.addBytesIn(bytesIn);
						acct.addBytesOut(bytesOut);
						acct.addBytesTotal(bytesTotal);

					}

				}

			}

		}

	}

	/**
	 * @param year
	 * @param month
	 * @return
	 */
	Iterator getIpAccountingIterator(int year, int month) {
		HashMap y = (HashMap) acctMap.get(new Integer(year));
		HashMap m = (HashMap) y.get(new Integer(month));
		return m.entrySet().iterator();
	}

	void show(int year, int month) {

		Iterator ipAcctIterator = getIpAccountingIterator(year, month);
		while (ipAcctIterator.hasNext()) {

			Map.Entry me3 = (Map.Entry) ipAcctIterator.next();
			String ip = (String) me3.getKey();
			Acct acct = (Acct) me3.getValue();

			String s = ip;
			for (int i = 0; i < replaces.length; i++)
				s = s.replaceAll(replaces[i][0], replaces[i][1]);

			System.out.println(year + "-" + month + " " + s + ": "
					+ Math.ceil(acct.getMegabytesTotal() * 10) / 10 + " MB/"
					+ Math.ceil(acct.getGigabytesTotal() * 100) / 100
					+ " GB -> " + Math.ceil(acct.getGigabytesTotal())
					+ " GB abrechnen");

		}

	}

	/**
	 * Report all accounting information to stdout
	 *  
	 */
	void showAll() {

		// Year
		Iterator acctMapIterator = acctMap.entrySet().iterator();
		while (acctMapIterator.hasNext()) {

			Map.Entry me = (Map.Entry) acctMapIterator.next();
			Integer year = (Integer) me.getKey();
			HashMap months = (HashMap) me.getValue();

			// Month
			Iterator monthsIterator = months.entrySet().iterator();
			while (monthsIterator.hasNext()) {

				Map.Entry me2 = (Map.Entry) monthsIterator.next();
				Integer month = (Integer) me2.getKey();
				HashMap ipAcct = (HashMap) me2.getValue();

				// IP accounting per month/per year
				show(year.intValue(), month.intValue());

			}

		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		boolean DEBUG = false;
		Calendar c = Calendar.getInstance();
		ArgumentParser ap = new ArgumentParser(args);
		// Debug
		ap.add("-d", null);
		// File
		ap.add("-f", "/export/home/rb/Maildir/.hostmaster.acct_mesh/cur");
		// Year
		ap.add("-y", c.get(Calendar.YEAR));
		// Month
		ap.add("-m", (c.get(Calendar.MONTH) + 1));
		ap.parse();

		ParseIpAccounting p = new ParseIpAccounting();

		File f = new File(ap.getString("-f"));
		File[] fa = f.listFiles();
		int oldYear = 0;
		int oldMonth = 0;

		for (int i = 0; i < fa.length; i++) {

			if (!fa[i].isFile())
				continue;

			c.setTimeInMillis(fa[i].lastModified());

			LangUtil.consoleDebug(DEBUG, "Processing file: " + fa[i].getName()
					+ c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1)
					+ "-" + c.get(Calendar.DAY_OF_MONTH));

			p.parseMesh(fa[i]);

		}

		if (ap.hasArgument("-y") && ap.hasArgument("-m"))
			p.show(ap.getInt("-y"), ap.getInt("-m"));
		else
			p.showAll();

	}

}