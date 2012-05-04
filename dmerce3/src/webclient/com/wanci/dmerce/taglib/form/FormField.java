package com.wanci.dmerce.taglib.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wanci.dmerce.forms.CONSTRAINT;
import com.wanci.dmerce.forms.CONSTRAINTS;
import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.OPTION;
import com.wanci.dmerce.forms.OPTIONS;

/**
 * FormField holt sich aus der forms.xml zum Einen das AttributeValue von
 * required, name, sowie die description und target und zum Anderen
 * constraints, die in einer Hashmap eingefügt werden. Die constraints sind für
 * die Validatoren wichtig, da dort die Elemente aufgelistet sind, die von den
 * Validatoren geprüft werden sollen. Weiterhin werden hier Fehlermeldungen in
 * einer ArrayList gesetzt und abgerufen.
 * 
 * @author pg
 * @author mm
 * @author mf
 * @version $Id: FormField.java,v 1.50 2004/03/19 14:27:34 rb Exp $
 */
public class FormField implements Cloneable {

	/**
	 * description of field
	 */
	private String description;
	/**
	 * fieldname
	 */
	private String name;
	/**
	 * fieldtype string, boolean, ...
	 */
	private String type;

	/**
	 * display type, you can set the preferred displaytype here e.g. type is
	 * boolean, default value is display as checkbox, you can set the display
	 * type to radio
	 */
	private String displayType;

	/**
	 * fieldvalues for list elements
	 */
	private String[] values;
	/**
	 * Page-ID des Feldes, muss nicht immer gesetzt sein
	 */
	private String pageId;
	/**
	 * switch if field is required
	 */
	private boolean required = false;
	/**
	 * constraints as maxlength, format, size
	 */
	private Map constraints;
	/**
	 * displayable values of a list
	 */
	private Map options;
	/**
	 * error messages of field will be assigned when validating the field
	 * object
	 */
	private ArrayList errorMsgs;
	/**
	 * die id des zugehörigen Formulars
	 */
	private String formId;
	/**
	 * Beschreibung von constraints (bisher nur bei format)
	 */
	private HashMap constraintsDesc;
	/**
	 * jaxb - field object
	 */
	private FIELD jaxbField;
	private String sql;
	private String sqlkey;
	private String sqlvalue;
	private String displayFormat;

	/**
	 * getter for type
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * setter for type
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * constructor erwartet das jaxb Objekt und die Formularid daraufhin
	 * initialisiert FormField selbständig alle Felder
	 * 
	 * @param f
	 * @param formId
	 */
	public FormField(FIELD f, String formId) {
		assert f != null : "Das übergebene FIELD darf nicht null sein";
		assert formId != null && !formId.equals("") : "Eine Formular-ID muss übergeben werden.";
		this.jaxbField = f;
		errorMsgs = new ArrayList();

		//required
		setRequired(f.isRequired());
		//name
		setName(f.getName());
		// description
		setDescription(f.getDescription());
		//type
		setType(f.getType());
		//display format
		setDisplayFormat(f.getDisplayformat());
		//display Type
		setDisplayType(f.getDisplaytype());
		// setzt die FormularId
		setFormId(formId);
		// Constraints auslesen und in der Map speichern
		constraints = new HashMap();
		constraintsDesc = new HashMap();

		CONSTRAINTS c = f.getConstraints();
		if (c != null) {
			List listConstraints = c.getConstraint();
			Iterator iterator = listConstraints.iterator();
			CONSTRAINT constraint;
			while (iterator.hasNext()) {
				constraint = (CONSTRAINT) iterator.next();
				String desc = constraint.getDescription();
				constraints.put(constraint.getType(), constraint.getValue());
				if (desc != null && !desc.equals(""))
					constraintsDesc.put(constraint.getType(), desc);
			}
		}

		OPTIONS o = f.getOptions();

		options = new LinkedHashMap();
		if (o != null) {
			sql = o.getSql();
			sqlkey = o.getSqlkey();
			sqlvalue = o.getSqlvalue();
			List listOptions = o.getOption();
			Iterator iterator = listOptions.iterator();
			OPTION option;
			while (iterator.hasNext()) {
				option = (OPTION) iterator.next();
				options.put(option.getKey(), option.getValue());
			}
		}
	}

	/**
	 * @param string
	 */
	private void setDisplayFormat(String format) {
		// TODO Auto-generated method stub
		this.displayFormat = format;
	}

	/**
	 * @return Returns the displayFormat.
	 */
	public String getDisplayFormat() {
		return displayFormat;
	}

	/**
	 * getter for description
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setter for description
	 * 
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * getter für required
	 * 
	 * @return required-flag
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * setter for required
	 * 
	 * @param required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * getter for name
	 * 
	 * @return name of field
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter for name
	 * 
	 * @param name
	 *            of field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt entsprechend dem Typ des Feldes einen Wert zurück als string
	 * 
	 * @return Wert des Feldes (niemals null)
	 */
	public String getValue() {
		if (values == null)
			return "";
		else {
			String output = "";
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null){
					if (!output.equals(""))
						output += ",";
					output += values[i];
				}
			}
			return output;
		}
	}

	/**
	 * getter for values gibt die werte eines feldes als String-array zurück
	 * 
	 * @return string array
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * setter for value
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if (value == null || value.equals("")) {
			this.values = null;
		} else {
			this.values = new String[1];
			this.values[0] = value;
		}
	}

	/**
	 * setter for values
	 * 
	 * @param values
	 */
	public void setValues(String[] values) {
		this.values = values;
	}

	/**
	 * getter for constraints
	 * 
	 * @return Ein Map Objekt mit den Inhalten vom constraints Element
	 */
	public Map getConstraints() {
		return constraints;
	}

	/**
	 * getter for a specific constraint
	 * 
	 * @return
	 */
	public String getConstraint(String name) {
		if (!getConstraints().containsKey(name))
			return "";
		else
			return (String) getConstraints().get(name);
	}

	/**
	 * getter for options, used within lists
	 * 
	 * @return Ein Map Objekt mit den Inhalten vom options Element
	 */
	public Map getOptions() {
		return options;
	}

	/**
	 * getter for errors
	 * 
	 * @return ArrayList Fehlermeldung
	 */
	public ArrayList getErrorMsgs() {
		return errorMsgs;
	}

	/**
	 * add an error message to the field, used by validators
	 * 
	 * @param string
	 */
	public void addErrorMsgs(String string) {
		errorMsgs.add(string);
	}

	/**
	 * Leert die Arraylist von errorMsgs
	 */
	public void clearErrorMsgs() {
		errorMsgs.clear();
	}

	/**
	 * getter for id
	 * 
	 * @return formid
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * setter for formid
	 * 
	 * @param formId
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * getter for constraints description, only used in format constraint
	 * 
	 * @return constraint-description (format description in Klartext)
	 */
	public HashMap getConstraintsDesc() {
		return constraintsDesc;
	}

	/**
	 * Erzeugt eine 1:1-Kopie des FormFields
	 * 
	 * @return Kopie des Objects
	 */
	public Object clone() {
		FormField newField = new FormField(this.jaxbField, getFormId());
		return newField;
	}

	/**
	 * getter for sql statement
	 * 
	 * @return sqlstatement
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * getter for sql key
	 * 
	 * @return String sql column which contains the keys
	 */
	public String getSqlkey() {
		return sqlkey;
	}

	/**
	 * getter for sql value column
	 * 
	 * @return sql column which contains the values
	 */
	public String getSqlvalue() {
		return sqlvalue;
	}

	/**
	 * getter for displaytype
	 * 
	 * @return element to display - radio, checkbox
	 */
	public String getDisplayType() {
		return displayType;
	}

	/**
	 * setter for display type
	 * 
	 * set the display type (radio, boolean) for the type
	 * 
	 * @param displayType
	 */
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	/**
	 * Setzt die Page-ID des Formularfeldes
	 * 
	 * @param pageId
	 */
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getPageId() {
		return this.pageId;
	}
	
	/**
	 * Gibt den ursprünglichen Namen der Datei zurück.
	 */
	public String getOriginalFileName() {
	    if (!getType().equals("file"))
	        throw new IllegalStateException("FormField: getOriginalFileName() ist nur bei Feldern vom Typ \"file\" erlaubt.");
	    return getValues()[1]; 
	}
	
	/**
	 * Gibt den Namen der Datei zurück, unter dem die Datei auf dem Server
	 * gespeichert ist.
	 */
	public String getServerFileName() {
	    if (!getType().equals("file"))
	        throw new IllegalStateException("FormField: getServerFileName() ist nur bei Feldern vom Typ \"file\" erlaubt.");
	    return getValues()[0]; 
	}
	

}