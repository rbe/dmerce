package com.wanci.dmerce.dbmgr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author tw
 *
 * @version $Id: PropertiesHandler.java,v 1.7 2003/10/30 16:50:50 pg Exp $
 */
public class PropertiesHandler {

	private static PropertiesHandler singleInstance = null;

	/**
	 * debug variable for debugging
	 */
	private boolean debug = false;

	/**
	 * connection key
	 */
	private String name = "";

	/**
	 * database system, e.g. oracle, mysql etc. 
	 */
	private String database = "";

	/**
	 * database driver, e.g. thin
	 */
	private String driver = "";

	/**
	 * user login for database
	 */
	private String user = "";

	/**
	 * password for database
	 */
	private String password = "";

	/**
	 * full qualified domain name of database host
	 */
	private String host = "";

	/**
	 * database port 
	 */
	private String port = "";

	/**
	 * database schema (mysql-database or oracle schema)
	 */
	private String schema = "";

	/**
	 * filename where the data is stored
	 */
	private String propertyFileName = "connection.ini";

	private PropertiesHandler() {

	}

	public static PropertiesHandler getSingleInstance() {

		if (singleInstance == null)
			singleInstance = new PropertiesHandler();

		return singleInstance;

	}

	public boolean load(String key) {

		if (key == null) {
			return false;
		}

		Properties props = new Properties();

		props = loadProperties();

		if (props == null)
			return false;

		if (props.containsKey(key)) {

			String connectionString = props.getProperty(key);

			String[] tokens = connectionString.split(":");

			setDatabase(tokens[0]);
			Util.debug(debug, "DATABASE: " + database);

			setDriver(tokens[1]);
			Util.debug(debug, "DRIVER: " + driver);

			setUser(tokens[2]);
			Util.debug(debug, "USER:" + user);

			setPassword(tokens[3]);
			Util.debug(debug, "PASSWORD: " + password);

			setHost(tokens[4]);
			Util.debug(debug, "HOST: " + host);

			setPort(tokens[5]);
			Util.debug(debug, "PORT: " + port);

			//if schema is empty an exception will be thrown
			if (tokens.length > 6)
				setSchema(tokens[6]);
			Util.debug(debug, "SCHEMA: " + schema);

		} else {
			return false;
		}

		return true;
	}

	/**
	 * load connections into properties-object
	 */
	public Properties loadProperties() {
		Properties props = new Properties();

		try {
			FileInputStream in = new FileInputStream(propertyFileName);
			props.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return props;
	}

	/**
	 * store 
	 * @param key
	 * @return
	 */
	public boolean store(String key) {
		String connectionString =
			database
				+ ":"
				+ driver
				+ ":"
				+ user
				+ ":"
				+ password
				+ ":"
				+ host
				+ ":"
				+ port
				+ ":"
				+ schema;

		//check if key already exists
		Properties props = loadProperties();
		if (props != null) {
			if (props.containsKey(key)) {
				System.out.println("Key exists");
				return false;
			}
		}

		//add the new connectionString to the properties-object				
		props.put(key, connectionString);

		//store all connections 
		try {
			FileOutputStream out = new FileOutputStream(propertyFileName);
			props.store(out, "DBMGR Connection File");

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {

			e.printStackTrace();
			return false;
		}

		//everything went fine
		return true;
	}

	/**
	 * getter for database
	 * @return
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * getter for driver
	 * @return
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * getter for host
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * getter for name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * getter for password
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * getter for port
	 * @return
	 */
	public String getPort() {
		return port;
	}

	/**
	 * getter for filename
	 * @return
	 */
	public String getPropertyFileName() {
		return propertyFileName;
	}

	/**
	 * getter for schema
	 * @return
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * getter for user
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * getter for database (oracle, mysql, ...)
	 * @param string
	 */
	public void setDatabase(String string) {
		database = string;
	}

	/**
	 * set driver
	 * @param string
	 */
	public void setDriver(String string) {
		driver = string;
	}

	/**
	 * set host
	 * @param string
	 */
	public void setHost(String string) {
		host = string;
	}

	/**
	 * set name
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * set password
	 * @param string
	 */
	public void setPassword(String string) {
		password = string;
	}

	/**
	 * set port
	 * @param string
	 */
	public void setPort(String string) {
		port = string;
	}

	/**
	 * setter for filename
	 * @param string
	 */
	public void setPropertyFileName(String string) {
		propertyFileName = string;
	}

	/**
	 * setter for db-schema
	 * @param string
	 */
	public void setSchema(String string) {
		schema = string;
	}

	/**
	 * setter for user
	 * @param string
	 */
	public void setUser(String string) {
		user = string;
	}

	/**
	 * usage example
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesHandler ph = new PropertiesHandler();

		//example: load connection 1
		if (ph.load("1"))
			System.out.println("Host for Connection 1 is " + ph.getHost());
		else {
			System.out.println("Error");
		}

		//example for storing a connection
		ph.setDatabase("oracle");
		ph.setDriver("thin");
		ph.setHost("gandalf");
		ph.setPassword("tw");
		ph.setUser("tw");
		ph.setPort("1521");
		ph.setSchema("wanci2");
		if (ph.store("2"))
			System.out.println("---OK---");
		else
			System.out.println("---NOT OK---");

		//lookup new connection
		if (ph.load("2"))
			System.out.println("Host for Connection 2 is " + ph.getHost());
		else {
			System.out.println("Error");
		}

	}

}