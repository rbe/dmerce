package com.wanci.dmerce.jdbcal;

import java.util.HashMap;
import java.util.Iterator;

import com.wanci.java.UtilUtil;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Table {

	private String name;

	private HashMap jdbcFields = new HashMap();
	
	public Table(String name) {
		setName(name);
	}

	public void addDmerceFields() {
		
        Field id = new Field("ID", new DatatypeNumber());
		id.setNotNull();
		id.setUniqueIndex();
		addField(id);

		Field createdDateTime =
			new Field("CreatedDateTime", new DatatypeNumber(16, 6));
		createdDateTime.setNotNull();
		createdDateTime.setIndex();
		addField(createdDateTime);

		Field createdBy =
			new Field("CreatedBy", new DatatypeNumber());
		createdBy.setIndex();
		addField(createdBy);

		Field changedDateTime =
			new Field("ChangedDateTime", new DatatypeNumber(16, 6));
		changedDateTime.setNotNull();
		changedDateTime.setIndex();
		addField(changedDateTime);

		Field changedBy =
			new Field("ChangedBy", new DatatypeNumber());
		changedBy.setIndex();
		addField(changedBy);

		Field active = new Field("active", new DatatypeNumber(1));
		active.setNotNull();
		active.setDefault(1);
		active.setIndex();
		addField(active);
        
	}

	public void addField(Field jd) {
		jdbcFields.put(jd.getName(), jd);
	}

	public void deleteField(String name) {
		jdbcFields.remove(name);
	}

	public Field getField(String name) {
		return (Field) jdbcFields.get(name);
	}

	public Iterator getFieldIterator() {
		return jdbcFields.entrySet().iterator();
	}

	public String getFieldNamesAsCommaString() {
		return UtilUtil.setKeysToCommaString(jdbcFields.entrySet());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null)
			this.name = name;
	}

}