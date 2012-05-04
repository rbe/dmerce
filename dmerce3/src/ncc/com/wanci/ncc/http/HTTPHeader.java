/**
 * Created on Feb 11, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.http;

import java.util.StringTokenizer;

/** Stellt einen HTTP-Header der Form
 * <p>
 * Name: Wert
 * </p>
 * <p>
 * dar
 * </p>
 * 
 * @author rb
 */
public class HTTPHeader {

	private String name;

	private Object value;

	public HTTPHeader() {
	}

	public HTTPHeader(String header) {
		StringTokenizer st = new StringTokenizer(header, ": ");
		setName(st.nextToken());
		setValue(st.nextToken());
	}

	public HTTPHeader(String name, Object value) {
		setName(name);
		setValue(value);
	}

	public HTTPHeader(String name, int value) {
		setName(name);
		setValue(value);
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setValue(int value) {
		this.value = new Integer(value);
	}

	public String toString() {
		return name + ": " + String.valueOf(value);
	}

}