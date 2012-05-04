/*
 * Created on Jul 23, 2004
 *
 */
package com.wanci.dmerce.kernel;

import java.util.HashMap;

/**
 * @author rb
 * @version $Id: Messages.java,v 1.1 2004/07/26 11:42:36 rb Exp $
 * 
 * Provides a static hashmap with all error codes <br>
 * <p>
 * Format: DMRC-SCCCC-LL
 * <p>
 * Explanaition: <br>
 * DMRC- is the prefix for all error codes within dmerce software family. S
 * describes the "section" where the error occured and CCCC is the error number.
 * The last "-LL" describes language code of the message.
 * 
 * The following section are known: <br>
 * 1 = dmerce Kernel <br>
 * 7 = StarOffice/OpenOffice addons
 *  
 */
public class Messages {

	public static HashMap errorMessages = new HashMap();

	private static final String prefix = "DMRC-";

	public static final int KERNEL = 1;
	
	public static final int WEBENGINE = 2;
	
	public static final int SQLWEBSERVICE = 3;

	public static final int OFFICE = 7;

	/**
	 * Error messages
	 */
	static {

		/*
		 * Kernel error messages
		 */
		error(KERNEL, 100, "en", "Database description is missing");
		error(KERNEL, 101, "en", "Cannot connect to database");

		/*
		 * Office error messages
		 */
		error(OFFICE, 1, "en", "Cannot connect to StarOffice/OpenOffice");

	}

	/**
	 * Register an error message
	 * 
	 * @param section
	 * @param errorCode
	 * @param countryCode
	 * @param errorDescription
	 */
	private static void error(int section, int errorCode, String countryCode,
			String errorDescription) {

		errorMessages.put(prefix + section + errorCode + "-"
				+ countryCode.toUpperCase(), errorDescription + "!");

	}

	/**
	 * Get an error message
	 * 
	 * @param section
	 * @param errorCode
	 * @return
	 */
	public static String getErrorMessage(int section, int errorCode) {

		String e = prefix + section + errorCode + "-EN";
		String msg = (String) errorMessages.get(e);

		if (msg == null)
			msg = "Sorry, error description for error code " + e
					+ " is missing!";

		return msg;
	}

}