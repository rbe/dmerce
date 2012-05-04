/*
 * Created on Apr 27, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.test;

import java.util.Iterator;
import java.util.StringTokenizer;

import com.wanci.java.LangUtil;

class LsLEntry {

	private String rights;

	private int linkCount;

	private String user;

	private String group;

	private long size;

	private int year;

	private String month;

	private int day;

	private String time;

	private String name;

	private StringTokenizer entry;

	public LsLEntry(String entry) {
		this.entry = new StringTokenizer(entry);
		analyse();
	}

	private void analyse() {

		rights = entry.nextToken();
		linkCount = Integer.parseInt(entry.nextToken());
		user = entry.nextToken();
		group = entry.nextToken();
		size = Long.parseLong(entry.nextToken());
		month = entry.nextToken();
		day = Integer.parseInt(entry.nextToken());

		String tmp = entry.nextToken();
		if (tmp.charAt(2) == ':')
			time = tmp;
		else
			year = Integer.parseInt(tmp);

		name = entry.nextToken();

	}

	public void dump() {

		System.out.print(
			"name="
				+ name
				+ " rights="
				+ rights
				+ " size="
				+ size
				+ " KB="
				+ getSizeInKb()
				+ " MB="
				+ getSizeInMb()
				+ " GB="
				+ getSizeInGb()
				+ " date="
				+ month
				+ " "
				+ day
				+ " "
				+ getYear()
				+ " ");
		if (year == 0)
			System.out.println(time);

	}

	/**
	 * @return
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @return
	 */
	public int getLinkCount() {
		return linkCount;
	}

	/**
	 * @return
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getRights() {
		return rights;
	}

	/**
	 * @return
	 */
	public long getSize() {
		return size;
	}

	public double getSizeInKb() {
		return size / 1024;
	}

	public double getSizeInMb() {
		return size / 1024 / 1024;
	}

	public double getSizeInGb() {
		return size / 1024 / 1024 / 1024;
	}

	/**
	 * @return
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return
	 */
	public int getYear() {
		if (year > 0)
			return year;
		else {
            return -1;
        }
	}

}

/**
 * @author rb
 *
 * Parsen der Ausgabe von ls -l in einen Vector, 
 * der Objekte enthaelt, die eine ls-Zeile ausgewertet haben
 * und beschreiben 
 *
 */
public class LsFilter implements SystemCommandFilter {

	private Iterator i;

	public LsFilter() {
	}

	public LsFilter(Iterator i) {
		this.i = i;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.SystemCommandFilter#analyse()
	 */
	public void analyse() {

		i.next();

		while (i.hasNext()) {

			String s = (String) i.next();
			LsLEntry e = new LsLEntry(s);
			e.dump();

		}

	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.SystemCommandFilter#iterator()
	 */
	public Iterator iterator() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.SystemCommandFilter#set(java.util.Vector)
	 */
	public void set(Iterator i) {
        this.i = i;
	}

	public static void main(String[] args) {

		LsFilter l = new LsFilter(LangUtil.systemCommand("ls -l /etc"));
		l.analyse();

	}

}