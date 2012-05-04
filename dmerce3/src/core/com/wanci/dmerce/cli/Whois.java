/*
 * Created on 23.01.2004
 *  
 */
package com.wanci.dmerce.cli;

import java.io.IOException;

import com.wanci.dmerce.exceptions.WhoisServerNotFoundException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.ncc.whois.WhoisAnswerHandler;
import com.wanci.ncc.whois.WhoisQuery;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: Whois.java,v 1.5 2004/03/05 12:30:12 rb Exp $
 * 
 * Command line interface for whois queries
 *  
 */
public class Whois {

	/**
	 * CLI: usage
	 *  
	 */
	public static void usage() {
		System.out.println("\nusage: whois <domain.tld>\n");
	}

	/**
	 * CLI
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {

		Boot.printCopyright("WHOIS QUERY");

		if (args.length != 1) {
			usage();
			System.exit(2);
		}

		WhoisQuery w = new WhoisQuery(args[0]);
		w.setDebug(true);
		try {
			w.query();
		}
		catch (WhoisServerNotFoundException e) {
			System.out.println(
				"\nERROR: Cannot find whois server '"
					+ w.getWhoisServer()
					+ "'\n");
		}
		catch (IOException e) {
			System.out.println(
				"\nERROR: Cannot communicate with whois server '"
					+ w.getWhoisServer()
					+ "'\n");
		}
		LangUtil.dumpVector(w.getAnswer());

		WhoisAnswerHandler hdl = w.getHandler();
		if (hdl != null) {

			String ref = hdl.getReferralWhoisServer();
			if (ref != null) {
				System.out.println("Referral whois server: " + ref);
				w.setWhoisServer(ref);
				try {
					w.query();
				}
				catch (WhoisServerNotFoundException e1) {
					System.out.println(
						"\nERROR: Cannot find whois server '"
							+ w.getWhoisServer()
							+ "'\n");
				}
				catch (IOException e1) {
					System.out.println(
						"\nERROR: Cannot communicate with whois server '"
							+ w.getWhoisServer()
							+ "'\n");
				}
				LangUtil.dumpVector(w.getAnswer());
			}

			System.out.print("\n\nDomain is ");
			if (hdl.isDomainAvailable())
				System.out.println("available");
			else
				System.out.println("unavailable");

		}

	}

}
