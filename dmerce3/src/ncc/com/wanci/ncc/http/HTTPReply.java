/**
 * Created on Feb 11, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class HTTPReply {

	private Socket socket;

	private InputStream is;

	private OutputStream os;

	private String url;

	private byte[] content;

	public HTTPReply() {
	}

	public HTTPReply(Socket socket) {
		setSocket(socket);
	}

	public HTTPReply(String url, byte[] content) {
		setUrl(url);
		setContent(content);
	}

	public byte[] getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	public void read() throws InterruptedException {

		int bytesFromRemote = 0;

		while (bytesFromRemote == 0) {

			try {

				//InputStreamReader isr = new InputStreamReader(is);
				int i = 1;

				while ((i = is.available()) > 0) {

					byte[] b = new byte[i];
					is.read(b, 0, i);
					bytesFromRemote += i;

					Thread.sleep(50);

				}

			}
			catch (IOException e) {
				//System.out.println("readAndSend(): when reading from server: " + e.getMessage());
			}

		}

	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public void setInput(InputStream is) {
		this.is = is;
	}

	public void setOutput(OutputStream os) {
		this.os = os;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}