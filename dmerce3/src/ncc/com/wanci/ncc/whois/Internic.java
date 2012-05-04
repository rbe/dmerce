/*
 * Created on 23.01.2004
 *  
 */
package com.wanci.ncc.whois;

import java.util.Iterator;
import java.util.StringTokenizer;

//WHOIS QUERY
//
//Querying bensmann.com at whois.internic.net:43
//
//Whois Server Version 1.3
//
//Domain names in the .com and .net domains can now be registered
//with many different competing registrars. Go to http://www.internic.net
//for detailed information.
//
//   Domain Name: BENSMANN.COM
//   Registrar: CORE INTERNET COUNCIL OF REGISTRARS
//   Whois Server: whois.corenic.net
//   Referral URL: http://www.corenic.net
//   Name Server: IRB.NS1.1XSP.COM
//   Name Server: IRB.NS2.1XSP.COM
//   Name Server: IRB.NS3.1XSP.COM
//   Status: ACTIVE
//   Updated Date: 13-apr-2003
//   Creation Date: 29-apr-1999
//   Expiration Date: 29-apr-2004
//
//
//>>> Last update of whois database: Fri, 23 Jan 2004 06:36:44 EST <<<
//
//The Registry database contains ONLY .COM, .NET, .EDU domains and
//Registrars.
//
//WHOIS QUERY
//
//Querying bensmann2.com at whois.internic.net:43
//
//Whois Server Version 1.3
//
//Domain names in the .com and .net domains can now be registered
//with many different competing registrars. Go to http://www.internic.net
//for detailed information.
//
//No match for "BENSMANN2.COM".
//
//>>> Last update of whois database: Fri, 23 Jan 2004 06:36:44 EST <<<
//

/**
 * @author rb
 * @version $Id: Internic.java,v 1.1 2004/02/02 09:41:50 rb Exp $
 *  
 */
public class Internic implements WhoisAnswerHandler {

	WhoisQuery whoisQuery;

	String referralWhoisServer;

	public Internic(WhoisQuery whoisQuery) {
		this.whoisQuery = whoisQuery;
		lookForWhoisServer();
	}

	/**
	 * @return
	 */
	public String getReferralWhoisServer() {
		return referralWhoisServer;
	}

	/**
	 * Search for whois server in answer from whois.internic.net
	 */
	void lookForWhoisServer() {

		referralWhoisServer = null;

		Iterator i = whoisQuery.getAnswer().iterator();
		while (i.hasNext()) {

			String line = (String) i.next();
			if (line.length() > 0) {

				if (line.charAt(0) == ' ') {

					StringTokenizer st = new StringTokenizer(line, ":");
					String left = st.nextToken();
					String right = st.nextToken();

					if (left.indexOf("Whois Server") > 0)
						referralWhoisServer = right.trim();

				}

			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.ncc.whois.WhoisAnswerHandler#isDomainAvailable()
	 */
	public boolean isDomainAvailable() {

		boolean available = false;

		Iterator i = whoisQuery.getAnswer().iterator();
		while (i.hasNext()) {

			String line = (String) i.next();
			if (line.indexOf("No match") >= 0)
				available = true;

		}

		return available;

	}

}