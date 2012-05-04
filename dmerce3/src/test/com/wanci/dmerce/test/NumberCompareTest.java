/*
 * Created on 08.09.2003
 *
 */
package com.wanci.dmerce.test;

/**
 * @author pg
 * @version $Id: NumberCompareTest.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 *
 */
public class NumberCompareTest {

	public static void main(String[] args) {
		String stringNumber = "60";
		String otherNumber = "70";
		
		Double val1 = new Double(stringNumber);
		Double val2 = new Double(otherNumber);
		
		System.out.println(val1.compareTo(val2));
	}
}
