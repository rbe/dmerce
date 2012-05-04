/*
 * Created on 04.07.2004
 *
 */
package com.wanci.customer.callando;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $$Id: Configuration.java,v 1.2 2004/07/16 13:36:31 rb Exp $$
 * 
 * Singleton for configuration settings
 */
public class Configuration {

	private static Configuration singleton;

	public boolean DEBUG = true;

	public boolean DEBUG2 = false;

	public ArgumentParser argParser;

	public Database jdbcDatabase;

	private Configuration() {
	}

	public static Configuration getInstance() {
		if (singleton == null) {
			singleton = new Configuration();
		}
		return singleton;
	}

	/**
	 * Customized debug message; we always display realm id and prefix
	 * 
	 * @param debug
	 * @param o
	 * @param str
	 */
	public static void debugMessage(boolean debug, Object o, String str) {
		LangUtil.consoleDebug(debug, o, str);
	}

}