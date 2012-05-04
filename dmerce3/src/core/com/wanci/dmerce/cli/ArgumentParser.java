/*
 * Created on Apr 3, 2003
 *  
 */
package com.wanci.dmerce.cli;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author rb
 * @version $Id: ArgumentParser.java,v 1.11 2004/07/16 13:45:38 rb Exp $
 *  
 */
public class ArgumentParser {

	/**
	 *  
	 */
	private String[] args;

	/**
	 *  
	 */
	private HashMap parameterValues = new HashMap();

	/**
	 *  
	 */
	private HashMap defaultValues = new HashMap();

	/**
	 *  
	 */
	private HashMap description = new HashMap();

	/**
	 * Constructor takes command line arguments (as from static void
	 * main(String[] args) as argument. The given value is parsed.
	 * 
	 * @param argv
	 */
	public ArgumentParser(String[] args) {
		this.args = args;
	}

	/**
	 * @param parameter
	 * @param defaultValue
	 */
	public void add(String parameter, String defaultValue) {

		parameterValues.put(parameter, null);
		defaultValues.put(parameter, defaultValue);

	}

	/**
	 * @param parameter
	 * @param defaultValue
	 * @param description
	 */
	public void add(String parameter, String defaultValue, String description) {

		add(parameter, defaultValue);
		this.description.put(parameter, description);

	}

	/**
	 * @param parameter
	 * @param defaultValue
	 */
	public void add(String parameter, int defaultValue) {

		parameterValues.put(parameter, null);
		defaultValues.put(parameter, new Integer(defaultValue));

	}

	/**
	 * @param parameter
	 * @param defaultValue
	 * @param description
	 */
	public void add(String parameter, int defaultValue, String description) {

		add(parameter, defaultValue);
		this.description.put(parameter, description);

	}

	/**
	 * Does a given parameter contain a value (e.g. substring) Example:
	 * Parameter -o has value 1,2,3 and you want to know if the number 2 was
	 * given
	 * 
	 * @param parameter
	 * @param value
	 * @return
	 */
	public boolean containsValue(String parameter, String value) {

		boolean b = false;
		boolean pv = false;
		boolean dv = false;
		String s;

		s = (String) parameterValues.get(parameter);
		if (s != null) {

			if (s.indexOf(value) >= 0)
				pv = true;

		} else {

			s = (String) defaultValues.get(parameter);
			if (s != null)
				if (s.indexOf(value) >= 0)
					dv = true;

		}

		if (pv || dv)
			b = true;

		return b;

	}

	/**
	 * Dump parameters to stdout for debugging
	 *  
	 */
	public void dump() {

		Iterator i = parameterValues.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			String p = (String) entry.getKey();
			System.out.println(p + " default=" + defaultValues.get(p)
					+ " hasArgument()=" + hasArgument(p));
		}

	}

	/**
	 * Return original arguments from command line
	 * 
	 * @return String array with arguments
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * @param parameter
	 * @return
	 */
	public String getDescription(String parameter) {
		return (String) description.get(parameter);
	}

	/**
	 *  
	 */
	public int getInt(String parameter) {

		Integer value;
		String o = (String) parameterValues.get(parameter);

		if (o == null)
			value = (Integer) defaultValues.get(parameter);
		else
			value = new Integer(o);

		if (value != null)
			return value.intValue();
		else
			return -1;

	}

	/**
	 * @param parameter
	 * @return
	 */
	public String getString(String parameter) {

		String value = (String) parameterValues.get(parameter);

		if (value == null)
			value = (String) defaultValues.get(parameter);

		return value;

	}

	/**
	 * @param parameter
	 * @return
	 */
	public boolean hasArgument(String parameter) {

		if (parameterValues.get(parameter) != null)
			return true;
		else
			return false;

	}

	/**
	 * Parse given arguments after definition of parameters has been made
	 *  
	 */
	public void parse() {

		if (args != null) {

			for (int i = 0; i < args.length; i++) {

				Object option = args[i];

				if (parameterValues.containsKey(option))
					if (defaultValues.get(option) != null) {
						//System.out.println("1 option " + option);
						parameterValues.put(option, args[++i]);
					} else {
						//System.out.println("2 option " + option);
						parameterValues.put(option, "");
					}

			}

		}

	}

	/**
	 * Print out usage of parameters
	 *  
	 */
	public void usage() {

		System.out.println("usage:\n");

		Iterator i = parameterValues.keySet().iterator();
		while (i.hasNext()) {

			String k = (String) i.next();
			System.out.println(k + ":\t" + description.get(k));

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ArgumentParser paa;

		if (args.length == 0)
			paa = new ArgumentParser(new String[] { "-h", "-d", ";" });
		else
			paa = new ArgumentParser(args);

		paa.add("-h", null);
		paa.add("-d", ",");
		paa.add("-m", "csv.map");
		paa.add("-o", null);

		Calendar c = Calendar.getInstance();
		paa.add("-y", c.get(Calendar.YEAR));

		paa.parse();

		paa.dump();

		System.out.println("-y: " + paa.getInt("-y"));
		System.out.println("-h: " + paa.hasArgument("-h") + "/"
				+ paa.getString("-h"));
		System.out.println("-d: " + paa.hasArgument("-d") + "/"
				+ paa.getString("-d"));
		System.out.println("-z: " + paa.hasArgument("-z") + "/"
				+ paa.getString("-z"));
		System.out.println("-m: " + paa.hasArgument("-m") + "/"
				+ paa.getString("-m"));
		System.out.println("-o: " + paa.hasArgument("-o") + "/"
				+ paa.getString("-o") + "/" + paa.containsValue("-o", "1"));

	}
}