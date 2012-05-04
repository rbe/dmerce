/*
 * Created on Feb 11, 2003
 *
 */
package com.wanci.ncc.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.wanci.java.LangUtil;

/**  
 * @author rb
 * @version $Id: HTTPRequest.java,v 1.1 2004/02/02 09:41:54 rb Exp $
 * 
 * Liest einen HTTP-Request von einem InputStream und speichert diesen
 * 
 */
class HTTPRequest {

	//private Socket socket;

	private InputStream is;

	private boolean get;

	private boolean post;

	private HTTPHeaderCollection httpHeaderCollection;

	public HTTPRequest() {
	}

	public HTTPRequest(InputStream is) throws IOException {
		setInput(is);
	}

	public void p(String s) {
		System.out.println(new Date() + " " + s);
	}

	public boolean isGet() {
		return get;
	}

	public boolean isPost() {
		return post;
	}

	public String read() {
		StringBuffer sb = new StringBuffer();

		// first line
		String firstLine = readOneLine();
		if (firstLine.toUpperCase().indexOf("GET") == 0)
			setGet(true);
		else if (firstLine.toUpperCase().indexOf("POST") == 0)
			setPost(true);
		sb.append(firstLine);

		// header
		String header = readUntilCRLF2();
		readHeader(header);
		sb.append(header);

		// body
		String s = readUntilCRLF2();
		if (s.length() > 0)
			sb.append(s);

		return sb.toString();
	}

	public String readOneLine() {
		return readUntil(new String[] { "\r\n", "\n" });
	}

	public void readHeader(String header) {
		Vector v =
			LangUtil.stringTokensToVector(new StringTokenizer(header, "\r\n"));
		Iterator i = v.iterator();

		httpHeaderCollection = new HTTPHeaderCollection();

		while (i.hasNext()) {

			HTTPHeader h = new HTTPHeader((String) i.next());
			httpHeaderCollection.add(h);

		}
	}

	public String readUntil(String[] until) {
		boolean end = false;
		StringBuffer sb = new StringBuffer();
		byte[] b = new byte[1];

		while (!end) {

			try {

				int i = is.read(b);

				if (i <= 0) {
					end = true;
					continue;
				}
				else {

					sb.append(new String(b));

					for (int p = 0; p < until.length; p++) {

						if (sb.toString().endsWith(until[p])) {
							//p("StringBuffer ends with CRLF2 or LF2");
							end = true;
						}

					}

				}

			}
			catch (IOException e) {
				end = true;
			}

		}

		String s = sb.toString();
		if (s.length() > 0)
			p("Request from client:\n" + s);

		return s;
	}

	public String readUntilCRLF2() {
		return readUntil(new String[] { "\r\n\r\n", "\n\n" });
	}

	public void setGet(boolean get) {
		this.get = get;
		this.post = !get;
	}

	public void setInput(InputStream is) {
		this.is = is;
	}

	public void setPost(boolean post) {
		this.post = post;
		this.get = !post;
	}

}
