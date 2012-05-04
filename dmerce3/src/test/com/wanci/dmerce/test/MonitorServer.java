package com.wanci.dmerce.test;

import java.io.IOException;
import java.net.ServerSocket;
/**
 * Ein einfacher TestServer für die XML-Funktionalität des
 * Dmerce-Client-Monitors.<p>
 * Basiert auf dem Server des Java-Tutorials.<p>
 * @author Kastner,Ron
 */
public class MonitorServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }

        while (listening)
	    new MonitorServerThread(serverSocket.accept()).start();

        serverSocket.close();
    }
}
