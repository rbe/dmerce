/*
 * Created on Feb 6, 2003
 *
 */
package com.wanci.ncc.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

class HTTPRequestHeader {

	public HTTPRequestHeader() {
	}

}

class HTTPReplyHeader {

	public HTTPReplyHeader() {
	}

}

/**
 * @author rb
 * @version $Id: HTTPProxyThread.java,v 1.1 2004/02/02 09:41:54 rb Exp $
 *
 */
public class HTTPProxyThread extends Thread {

	private Socket clientSocket;

	private OutputStream outClient;

	//private Socket remoteSocket;

	private int remoteTimeout = 100;

	private BufferedInputStream inRemote;

	private OutputStream outRemote;

	private String clientIdent;

	private String remoteIdent;

	private HTTPRequest httpRequest;

	public HTTPProxyThread(Socket clientSocket) {
		super("HTTPProxyThread[" + clientSocket.getInetAddress() + "]");
		this.clientSocket = clientSocket;
		this.clientIdent =
			clientSocket.getInetAddress() + ":" + clientSocket.getPort();
	}

	public void p(String s) {
		System.out.println(
			new Date() + " [" + clientIdent + "<->" + remoteIdent + "] " + s);
	}

	private void createClientObjects() throws IOException {
		clientSocket.setSoTimeout(300);
		outClient = clientSocket.getOutputStream();
		p("Accepted connection from:" + clientIdent);
	}

	private void createRemoteObjects() throws IOException {
		String remoteServer = "www.1ci.de";

		Socket remoteSocket = new Socket(remoteServer, 80);
		remoteSocket.setSoTimeout(remoteTimeout);
		remoteSocket.setTcpNoDelay(true);
		remoteIdent =
			remoteSocket.getInetAddress() + ":" + remoteSocket.getPort();

		inRemote = new BufferedInputStream(remoteSocket.getInputStream());
		outRemote = remoteSocket.getOutputStream();

		p("Connection to remote server openend and streams established");
	}

	private void init() throws IOException {
		createClientObjects();
		createRemoteObjects();
	}

	private void readAndSend()
		throws InterruptedException, SocketException, IOException {

		//byte[] rq;

		p("Start reading from client");

		try {

			httpRequest = new HTTPRequest(clientSocket.getInputStream());
            //rq = httpRequest.read().getBytes();
			/*
			outRemote.write(req.read().getBytes());
			outRemote.flush();
			*/

		}
		catch (IOException e) {
			p("readAndSend(): when reading from client: " + e.getMessage());
		}

		p("Finished reading from client");

		//HTTPReply rep = new HTTPReply(remoteSocket);

		p("Start reading from remote");
		//p("Finished reading from remote (" + bytesFromRemote + ")");

	}

	public void run() {

		long t1 = System.currentTimeMillis();

		p("Start");

		try {
			init();
			readAndSend();
		}
		catch (IOException e1) {
			p("HTTPProxyThread.run(): " + e1.getMessage());
		}
		catch (InterruptedException ie) {
			p("HTTPProxyThread.run(): " + ie.getMessage());
		}

		try {
			clientSocket.close();
			p("Closed client socket");
		}
		catch (IOException e) {
			p("HTTPProxyThread.run(): " + e.getMessage());
		}

		long t2 = System.currentTimeMillis();

		p("Handling of request took " + ((t2 - t1) - remoteTimeout) + " ms");
		
		p("#Threads: " + Thread.activeCount());

	}

}
