/**
 * Created on Feb 11, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class HTTPHeaderCollection {

	private HashMap httpHeaders;

	public HTTPHeaderCollection() {
		httpHeaders = new HashMap();
	}

	public void add(HTTPHeader httpHeader) {
		httpHeaders.put(httpHeader.getName(), httpHeader);
	}

	public HTTPHeader get(String name) {
		return (HTTPHeader) httpHeaders.get(name);
	}

	public void remove(String name) {
		httpHeaders.remove(name);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		Set s = httpHeaders.entrySet();
		Iterator i = s.iterator();

		while (i.hasNext()) {

			HTTPHeader h = (HTTPHeader) i.next();
			sb.append(h.toString());

		}

		return sb.toString();
	}

}