/*
 * Created on Jan 30, 2003
 *
 */
package com.wanci.dmerce.jdbcal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author rb
 * @version $Id: TableRow.java,v 1.1 2004/03/29 13:39:31 rb Exp $
 *
 */
public class TableRow {

	private Table table;

	private HashMap values = new HashMap();

	public TableRow(Table table) {
		this.table = table;
	}

	public void addValue(Field jdbcField, Object value) {
		values.put(jdbcField, value);
	}

	public void addValue(Field jdbcField, int value) {
		values.put(jdbcField, new Integer(value));
	}

	public void addValue(Field jdbcField, float value) {
		values.put(jdbcField, new Float(value));
	}

	public void addValue(Field jdbcField, double value) {
		values.put(jdbcField, new Double(value));
	}

	public void addValue(Field jdbcField, byte value) {
		values.put(jdbcField, new Byte(value));
	}

	public void addValue(Field jdbcField, boolean value) {
		values.put(jdbcField, new Boolean(value));
	}

	public String getFieldNames() {

		StringBuffer sb = new StringBuffer();

		Set s = values.entrySet();
		Iterator i = s.iterator();

		while (i.hasNext()) {

			Map.Entry o = (Map.Entry) i.next();

			Field f = (Field) o.getKey();
			sb.append(f.getName());

			if (i.hasNext())
				sb.append(", ");

		}
		
		return sb.toString();

	}

	public String generateInsertStatement() {

		StringBuffer sb = new StringBuffer();
		sb
			.append("INSERT INTO ")
			.append(table.getName())
			.append(" (")
			.append(getFieldNames())
			.append(") VALUES (");
		
		Set s = values.entrySet();
		Iterator i = s.iterator();
		
		while (i.hasNext()) {
			
			Map.Entry o = (Map.Entry) i.next();
			
			Object v = o.getValue();
			sb.append(v);

			if (i.hasNext())
				sb.append(", ");
			
		}
		
		sb.append(")");
		
		return sb.toString();

	}

}