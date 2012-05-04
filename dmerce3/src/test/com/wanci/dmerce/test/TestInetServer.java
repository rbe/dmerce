package com.wanci.dmerce.test;

import java.io.IOException;
import java.net.ServerSocket;

import com.wanci.ncc.http.HTTPProxyThread;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestInetServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		ServerSocket proxySocket = new ServerSocket(3091);
		proxySocket.setReuseAddress(true);

		boolean running = true;
		while (running) {
			System.out.println("\n\n New HTTPProxyThread\n\n");
			new HTTPProxyThread(proxySocket.accept()).start();
		}

		proxySocket.close();

	}

}