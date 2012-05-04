/*
 * Created on 22.01.2004
 *  
 */
package com.wanci.ncc.whois;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import com.wanci.dmerce.exceptions.WhoisServerNotFoundException;

/**
 * @author rb
 * @version $Id: WhoisQuery.java,v 1.2 2004/03/05 12:32:00 rb Exp $
 *  
 */
public class WhoisQuery {

	/**
	 * Debug flag
	 */
	boolean DEBUG = false;

	/**
	 * Whois servers for certain TLDs
	 */
	static HashMap whoisServers = new HashMap();
	static {
		whoisServers.put("at", "whois.nic.at");
		whoisServers.put("ch", "whois.nic.ch");
		whoisServers.put("com", "whois.internic.net");
		whoisServers.put("de", "whois.denic.de");
		whoisServers.put("net", "whois.internic.net");
		whoisServers.put("org", "whois.publicinterestregistry.net");
		whoisServers.put("edu", "whois.internic.net");
		whoisServers.put("info", "whois.afilias.net");
		//whoisServers.put("eu", "");
		whoisServers.put("biz", "whois.neulevel.biz");
	}

	/**
	 * Server used for query
	 */
	String server;

	/**
	 * Whois server port; standard is 43
	 */
	int port = 43;

	/**
	 * Socket for communication
	 */
	Socket socket;

	/**
	 * Vector with String objects representing the answer from a whois server
	 */
	Vector answer = new Vector();

	/**
	 * Domain
	 */
	String domain;

	/**
	 * TLD of domain
	 */
	String tld;

	/**
	 * Constructor
	 */
	public WhoisQuery() {
	}

	/**
	 * Constructor
	 * 
	 * @param domain
	 */
	public WhoisQuery(String domain) {
		setDomain(domain);
	}

	/**
	 * Returns whois answer
	 * 
	 * @return
	 */
	public Vector getAnswer() {
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Return appropiate handler for certain whois server
	 * 
	 * @return
	 */
	public WhoisAnswerHandler getHandler() {

		WhoisAnswerHandler h = null;

		if (server.indexOf("internic") > 0)
			h = new Internic(this);
		else if (server.indexOf("denic") > 0)
			h = new Denic(this);

		return h;

	}

	/**
	 * Returns top level domain (part after last dot in domain)
	 * 
	 * @param domain
	 * @return String top level domain (without dot)
	 */
	String getTopLevelDomain(String domain) {
		return domain.substring(domain.lastIndexOf(".") + 1, domain.length());
	}

	/**
	 * Returns server which was queried for domain
	 * 
	 * @return
	 */
	public String getWhoisServer() {
		return server;
	}

	/**
	 * Read answer from whois server and store it in a vector
	 * 
	 * @param in
	 * @throws IOException
	 */
	Vector readAnswer(BufferedReader in) throws IOException {

		String line;
		while ((line = in.readLine()) != null) {
			answer.add(line);
		}

		return answer;

	}

	/**
	 * Connects to a whois server, sends a query and returns a BufferedReader
	 * for answer of whois server
	 * 
	 * @param domain
	 *            Domain to query at whois server
	 * @return BufferedReader Answer of whois server
	 */
	BufferedReader sendQuery() throws WhoisServerNotFoundException {

		BufferedReader in = null;
		if (server == null)
			throw new WhoisServerNotFoundException(
				"Don't know whois server for top level domain '" + tld + "'");

		if (DEBUG)
			System.out.println(
				"Querying " + domain + " at " + server + ":" + port);

		try {

			// Establish connection to whois server & port
			socket = new Socket(server, port);
			PrintStream out = new PrintStream(socket.getOutputStream());
			in =
				new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			// Send the whois query
			out.println(domain);

		}
		catch (java.net.UnknownHostException uhe) {
			// Unknown whois server
			throw new WhoisServerNotFoundException(
				"Whois: unknown host: " + server);
		}
		catch (java.net.ConnectException ce) {
			// Cannot connect to whois server
			throw new WhoisServerNotFoundException(
				"Whois: cannot connect to " + server);
		}
		catch (IOException ioe) {
			// Cannot connect to whois server
			throw new WhoisServerNotFoundException(
				"Whois: communication error with " + server);
		}

		return in;

	}

	/**
	 * Set debug on/off
	 * 
	 * @param debug
	 *            Boolean: true = debug on
	 */
	public void setDebug(boolean debug) {
		DEBUG = debug;
	}

	/**
	 * Setter for domain the whois server will be queried for. Cleares vector
	 * "answer" when called
	 * 
	 * @param domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
		tld = getTopLevelDomain(domain);
		server = (String) whoisServers.get(tld);
		answer.clear();
	}

	/**
	 * Explicitly set whois server to query
	 * 
	 * @param server
	 */
	public void setWhoisServer(String server) {
		this.server = server;
	}

	/**
	 * Public method that sends a query to a whois server and returns the
	 * answer as an vector
	 * 
	 * @param domain
	 * @return @throws
	 *         WhoisServerNotFoundException
	 * @throws IOException
	 */
	public void query() throws WhoisServerNotFoundException, IOException {
		readAnswer(sendQuery());
	}

}