/*
 * Datei angelegt am 13.01.2004
 */
package com.wanci.dmerce.test;

import java.util.Date;

import oracle.sql.DATE;

/**
 * OracleDateTypeTest
 *
 * @author Masanori Fujita
 */
public class OracleDateTypeTest {
	
	public static void main(String[] args) {
		DATE date;
		date = new DATE();
		Date date2 = date.dateValue();
		System.out.println(date2);
	}

}
