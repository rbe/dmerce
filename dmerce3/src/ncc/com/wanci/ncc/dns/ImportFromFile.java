/*
 * Created on Apr 27, 2003
 *  
 */
package com.wanci.ncc.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;
import com.wanci.dmerce.exceptions.ValidatorException;
import com.wanci.java.LangUtil;
import com.wanci.java.TimeUtil;

/**
 * @author rb
 * @version $Id: ImportFromFile.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 * 
 * ImportFromFile BIND zone files into nameserver and zone objects
 *  
 */
public class ImportFromFile {

	/**
	 *  
	 */
	private boolean DEBUG = true;

	/**
	 *  
	 */
	private BufferedReader reader;

	/**
	 *  
	 */
	private Zone zone;

	int nameserverPosition;

	/**
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public ImportFromFile(File filename) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(filename));
	}

	/**
	 * @param line
	 */
	private void createResourceRecord(String line)
		throws
			NumberFormatException,
			ResourceRecordInvalidException,
			ValidatorException {

		StringTokenizer st = new StringTokenizer(line);
		String name = null;
		String ttl = null;
		String type = null;
		String mxPriority = null;
		String value = null;

		switch (st.countTokens()) {

			case 3 :
				st.nextToken(); // IN
				type = st.nextToken();
				value = st.nextToken();
				break;

			case 4 :
				name = st.nextToken();
				st.nextToken(); // IN
				type = st.nextToken();
				value = st.nextToken();
				break;

			case 5 :
				String first = st.nextToken();
				//System.out.println("first = " + first);
				try {
					int i = Integer.parseInt(first);
					ttl = first;
					//System.out.println("ttl = " + first);
				}
				catch (NumberFormatException e) {
					name = first;
					//System.out.println("name = " + first);
				}

				String second = st.nextToken();
				if (!second.equalsIgnoreCase("in")) {

					//System.out.println("second = " + second);
					try {
						int i = Integer.parseInt(second);
						ttl = second;
						//System.out.println("ttl = " + second);
						// IN
						st.nextToken();
					}
					catch (NumberFormatException e) {
						// IN
						st.nextToken();
					}

				}

				type = st.nextToken();
				if (type.equalsIgnoreCase("ptr"))
					name = first;

				if (st.countTokens() > 1)
					mxPriority = st.nextToken();

				value = st.nextToken();

				break;
		}

		/*
		 * System.out.println("-> ttl=" + ttl + " name=" + name + "type=" +
		 * type + " mxPrio=" + mxPriority + " value=" + value);
		 */

		int nttl;
		if (ttl != null)
			nttl = new Integer(ttl).intValue();
		else
			nttl = 10800;

		if (type.equalsIgnoreCase("ns")) {
			zone.addNameserverRecord(
				new NameserverRecord(value, "", ++nameserverPosition));
		}
		else if (type.equalsIgnoreCase("a")) {
			zone.addResourceRecord(new NameToAddressRecord(name, value, nttl));
		}
		else if (type.equalsIgnoreCase("ptr")) {
			zone.addResourceRecord(new AddressToNameRecord(name, value, nttl));
		}
		else if (type.equalsIgnoreCase("cname")) {
			zone.addResourceRecord(new CanonicalNameRecord(name, value, nttl));
		}
		else if (type.equalsIgnoreCase("mx")) {

			zone.addResourceRecord(
				new MailExchangerRecord(
					value,
					nttl,
					new Integer(mxPriority).intValue()));

		}

	}

	/**
	 * @param line
	 */
	private void createSoaRecord(String line) {

		StringTokenizer st = new StringTokenizer(line);
		if (st.countTokens() == 12) {

			String name = st.nextToken();
			st.nextToken(); // IN
			st.nextToken(); // SOA
			String primaryNameserver = st.nextToken();
			String zoneContact = st.nextToken();
			st.nextToken(); // (
			String serial = st.nextToken();
			String refresh = st.nextToken();
			String retry = st.nextToken();
			String expire = st.nextToken();
			String negativeCachingTtl = st.nextToken();

			/*
			 * System.out.println( "name=" + name + " pns=" + primaryNameserver + "
			 * zonec=" + zoneContact + " serial=" + serial + " refresh=" +
			 * refresh + " retry=" + retry + " expire=" + expire + " ncttl=" +
			 * negativeCachingTtl);
			 */

			zone = new Zone(name);

			SOA soa = new SOA(name, primaryNameserver, zoneContact);
			soa.setSubserial(1);
			soa.setExpire(new Integer(expire).intValue());
			soa.setRefresh(new Integer(refresh).intValue());
			soa.setRetry(new Integer(retry).intValue());
			soa.setNegativeCachingTtl(
				new Long(TimeUtil.descriptionToSeconds(negativeCachingTtl))
					.intValue());

			zone.setSOA(soa);

		}

	}

	/**
	 * @return
	 */
	public Zone getZone() {
		return zone;
	}

	/**
	 * Liest ein Zonefile ein und stellt ein Objekt vom Typ "Zone" zusammen
	 * 
	 * @return @throws
	 *         IOException
	 * @throws NumberFormatException
	 * @throws ResourceRecordInvalidException
	 * @throws ValidatorException
	 */
	public Zone recreate()
		throws
			IOException,
			NumberFormatException,
			ResourceRecordInvalidException,
			ValidatorException {

		String line;
		while ((line = reader.readLine()) != null) {

			if (line.indexOf("IN") > 0) {
				// Wenn SOA gefunden, bis zum nächsten ")" einlesen
				if (line.indexOf("SOA") > 0) {

					StringBuffer soaLines = new StringBuffer(line);
					// wenn kein ) in der aktuellen Zeile gefunden
					if (line.indexOf(")") == -1)
						while ((line = reader.readLine()) != null) {

							int semicolon = line.indexOf(";");
							if (semicolon > 0)
								line = line.substring(0, semicolon - 1);

							soaLines.append(line);

							if (line.indexOf(")") > 0)
								break;

						}

					createSoaRecord(soaLines.toString());

				}
				else if (
					line.indexOf("NS") > 0
						|| line.indexOf("A") > 0
						|| line.indexOf("CNAME") > 0
						|| line.indexOf("MX") > 0
						|| line.indexOf("PTR") > 0)
					try {
						createResourceRecord(line);
					}
					catch (Exception e) {
						LangUtil.consoleDebug(
							DEBUG,
							"Could not create resource record from line: "
								+ line
								+ ". Exception: "
								+ e.getCause()
								+ ": "
								+ e.getMessage());

					}

			}

		}

		return zone;

	}

}