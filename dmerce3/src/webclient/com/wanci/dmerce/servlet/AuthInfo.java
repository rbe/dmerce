/*
 * Created on 12.09.2004
 *
 */
package com.wanci.dmerce.servlet;

import java.math.BigInteger;
import java.util.Map;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * @author rb
 * @version $Id$
 *  
 */
public class AuthInfo {

	private String username;

	private String realm;

	private Map dataFields;

	private int activeSessionsCount = 0;

	/**
	 * 
	 * @param dataFields
	 */
	public AuthInfo(String username, Map dataFields)
			throws XmlPropertiesFormatException {

		this.username = username;
		this.dataFields = dataFields;
		this.realm = getDataField(XmlPropertiesReader.getInstance()
				.getProperty("auth.realmfield"));

	}

	public int getActiveSessionsCount() {
		return activeSessionsCount;
	}

	public String getDataField(String field) {

		Object o = dataFields.get(field);

		if (o instanceof String)
			return (String) dataFields.get(field);
		else if (o instanceof Integer | o instanceof BigInteger
				| o instanceof Float | o instanceof Double)
			return String.valueOf(dataFields.get(field));
		else
			return "unknown";

	}

	public String getUsername() {
		return username;
	}

	public String getRealm() {
		return realm;
	}

	public void setActiveSessionsCount(int count) {
		activeSessionsCount = count;
	}

}