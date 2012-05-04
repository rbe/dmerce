/*
 * Created on 26.01.2004
 *  
 */
package com.wanci.ncc.whois;

import java.util.Iterator;

/**
 * @author rb
 * @version ${Id}
 *  
 */
public class Denic implements WhoisAnswerHandler {

	WhoisQuery whoisQuery;

	public Denic(WhoisQuery whoisQuery) {
		this.whoisQuery = whoisQuery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanci.dmerce.ncc.whois.WhoisAnswerHandler#getReferralWhoisServer()
	 */
	public String getReferralWhoisServer() {
		return null;
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
			if (line.indexOf("free") >= 0)
				available = true;

		}

		return available;
		
	}

}