/*
 * Created on 08.10.2004
 *
 */
package com.wanci.dmerce.taglib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id$
 *  
 */
public class TagOutputFormatter {

	private boolean DEBUG = false;

	private boolean DEBUG2 = false;

	private String format;

	private int minPrecision = -1;

	private int maxPrecision = -1;

	private int precision = -1;

	public TagOutputFormatter(String format) {
		this.format = format;
		init();
	}

	public TagOutputFormatter() {
		init();
	}

	private void init() {

		try {

			DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"debug");
			DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");

		} catch (XmlPropertiesFormatException e) {
		}

	}

	/**
	 * Formatiert die Ausgabe des Tags in Abhängigkeit des anzuzeigenden
	 * Datentyps
	 * 
	 * @param fieldobject
	 * @return
	 */
	public String format(Object fieldobject, String fieldName) {

		String value;

		LangUtil.consoleDebug(DEBUG2, this, "format(): Formatting field '"
				+ fieldName + "' value=" + fieldobject.toString() + " type="
				+ fieldobject.getClass());

		// Calendar-Objekte dürfen nicht angezeigt werden, sondern müssen
		// formatiert werden
		if (fieldobject instanceof Calendar) {

			LangUtil
					.consoleDebug(DEBUG2, this, "format(): instanceof Calendar");

			SimpleDateFormat sdf;
			if (format != null && !format.equals(""))
				sdf = new SimpleDateFormat(format);
			else
				sdf = new SimpleDateFormat();

			value = sdf.format(((Calendar) fieldobject).getTime());

		} else if (fieldobject instanceof Double
				| fieldobject instanceof Integer
				| fieldobject instanceof BigInteger
				| fieldobject instanceof BigDecimal) {

			LangUtil.consoleDebug(DEBUG2, this,
					"format(): instanceof Double | Integer | BigInteger | BigDecimal."
							+ " precision=" + precision + " minprecision="
							+ minPrecision + " maxprecision=" + maxPrecision);

			// Falls keine Precision angegeben worden ist,
			// direkt aus der Datenbank übernehmen
			if (maxPrecision == -1 & minPrecision == -1 & precision == -1)
				value = fieldobject.toString();
			else {

				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(maxPrecision);
				nf.setMinimumFractionDigits(minPrecision);

				nf.setGroupingUsed(true);

				if (fieldName.equalsIgnoreCase("id") || minPrecision <= 0
						|| maxPrecision <= 0)
					nf.setGroupingUsed(false);

				LangUtil.consoleDebug(DEBUG2, this,
						"format(): formatting value using NumberFormat. groupingUsed="
								+ nf.isGroupingUsed());

				//value = nf.format(((Double) fieldobject).doubleValue());
				value = nf.format(fieldobject);

			}

		} else {

			LangUtil.consoleDebug(DEBUG2, this, "format(): everything else");
			value = fieldobject.toString();

			// RB20041008: Newline to HTML (<br>)
			if (format != null && value != null) {
				if (format.equalsIgnoreCase("html"))
					value = value.replaceAll("\n", "<br>");
			}

		}

		return value;

	}
	
	public void setFormat(String format) {
		this.format=format;
	}

	public void setMinPrecision(int minPrecision) {
		this.minPrecision = minPrecision;
	}

	public void setMaxPrecision(int maxPrecision) {
		this.maxPrecision = maxPrecision;
	}

}