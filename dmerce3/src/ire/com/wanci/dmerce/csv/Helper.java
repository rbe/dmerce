/*
 * Created on Jul 17, 2003
 *
 */
package com.wanci.dmerce.csv;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author rb
 * @version $Id: Helper.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * Hilfsfunktionen fuer CSV<->SQL
 * 
 */
public class Helper {

	/**
	 * 
	 */
	private Mapping csvMapping;

	/**
	 * 
	 */
	private CsvHeader csvHeader;

	/**
	 * 
	 */
	private CsvSettings csvSettings;

	/**
	 * 
	 *
	 */
	public Helper() {
	}

	/**
	 * 
	 * @param csvSettings
	 * @param csvHeader
	 * @param csvMapping
	 */
	public Helper(
		CsvSettings csvSettings,
		CsvHeader csvHeader,
		Mapping csvMapping) {

		this.csvSettings = csvSettings;
		this.csvHeader = csvHeader;
		this.csvMapping = csvMapping;

	}
	/**
	 * Liefert alle SQL-Felder als durch Komma getrennten String
	 * zurueck, die laut CSV-Header und Mapping benoetigt werden
	 * 
	 * @return
	 */
	public String getFieldNamesSeparatedByComma() {

		StringBuffer sb = new StringBuffer();

		Iterator fieldNamesIterator = csvMapping.getFieldNameMapIterator();
		while (fieldNamesIterator.hasNext()) {

			Map.Entry m = (Map.Entry) fieldNamesIterator.next();
			String csvFieldName = (String) m.getKey();
			String sqlFieldName = csvMapping.getMappedFieldName(csvFieldName);

			sb.append(sqlFieldName);
			if (fieldNamesIterator.hasNext())
				sb.append(", ");

		}

		return sb.toString();

	}

	/**
	 * 
	 * Parst ein Datum aus einem String (CSV Daten) und gibt eine
	 * entsprechendes java.sql.Date zurueck 
	 * 
	 * @param data
	 * @return
	 */
	public Date parseGermanDate(String data) throws ParseException {

		DateFormat df =
			DateFormat.getDateInstance(DateFormat.SHORT, new Locale("de"));
		df.parse(data);

		Calendar c = df.getCalendar();
		c.setTimeZone(TimeZone.getTimeZone("GMT+1"));
		c.setFirstDayOfWeek(Calendar.MONDAY);

		Date d = new Date(c.getTimeInMillis());
		//System.out.println(data + " => " + d.toString());

		return d;

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws ParseException {
		System.out.println(new Helper().parseGermanDate("20.09.1978"));
	}

}