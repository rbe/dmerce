/*
 * Created on 15.02.2004
 *  
 */
package com.wanci.dmerce.test;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author rb
 * @version $Id: DecimalFormatTest.java,v 1.3 2004/06/03 23:51:54 rb Exp $
 *  
 */
public class DecimalFormatTest {

	public static void main(String[] args) {

		NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(true);
		System.out.println(nf.format(1234));
		
	}

}