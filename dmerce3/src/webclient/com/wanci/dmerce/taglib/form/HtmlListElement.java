/*
 * Created on Nov 5, 2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.taglib.form;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.QResult;
import com.wanci.dmerce.webservice.db.QRow;
import com.wanci.java.LangUtil;

/**
 * text element
 * 
 * @author pg
 */
public class HtmlListElement extends AbstractHtmlFormElement {

    private static boolean DEBUG = false;

    private String type = "combo";

    private boolean multiple;

    private LinkedHashMap options;

    /**
     * @param field
     */
    public HtmlListElement(FormField field) {
        super(field);
    }

    /**
     * output method for displaying html text input field
     * 
     * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
     */
    public String toHtml() {

        String output = "";

        if (checkForAttribute("multiple")) {
            //then we build either a checkbox list or a listbox or a forced
            // radiobuttongroup with 2 columns (yes/no)
            multiple = true;
        }
        else {
            //then the type is a combobox, a listbox or a radiobuttongroup
            multiple = false;
        }

        //LangUtil.consoleDebug(true, "TYPE is " + type);

        if (type.equals("combo") || type.equals("list")) {
            output = "<select name=\"" + field.getName() + "\"";
            output += attributesToHtml();
            //default size for lists
            if (type.equals("list") && !checkForAttribute("size"))
                output += "size=\"5\"";
            output += ">\n";
        }
        else if (type.equals("radio")) {
            if (multiple)
                output += "<table><tr><th>Ja</th><th>Nein</th><th>&nbsp;</th></tr>\n";
        }

        //sqlservice

        if (options == null) {
            //check if there is a sql-directive in options tag
            if (field.getSql() != null) {
                options = new LinkedHashMap();
                try {
                    SQLService query = new SQLService();

                    QResult result = query.executeQuery(field.getSql());
                    if (!result.success()) {
                        //TaglibHelper.errorPage(pageContext,
                        // result.getErrorMessage());
                    }
                    Iterator qResultIterator = result.getRows().iterator();

                    while (qResultIterator.hasNext()) {
                        QRow currentQRow = (QRow) qResultIterator.next();
                        HashMap fields = (HashMap) currentQRow.getFields();
                        if (field.getSqlkey() != null
                            && field.getSqlvalue() != null) {
                            options.put(fields.get(field.getSqlkey())
                                .toString(), fields.get(field.getSqlvalue())
                                .toString());
                        }
                        else {
                            Vector v = (Vector) fields.values();
                            options.put(v.get(0).toString(), v.get(1)
                                .toString());
                        }
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                //easiest case: check if there are options in the field
                if (field.getOptions() != null) {
                    options = (LinkedHashMap) field.getOptions();
                }

            } //if sql present or not

        } //if options == null

        //go through all keys
        if (options != null) {

            Iterator iterator = options.keySet().iterator();
            while (iterator.hasNext()) {

                String elementKey = (String) iterator.next();
                LangUtil.consoleDebug(DEBUG, "HtmlListElement: elementKey");
                String val = (String) options.get(elementKey);
                boolean match = false;

                //check if value matches values
                if (values != null) {
                    //check if the key is in values stringlist
                    for (int i = 0; i < values.length; i++) {
                        //LangUtil.consoleDebug(true, "KEY is " + elementKey +
                        // " VALUE is " + values[i]);
                        if (elementKey.equals(values[i]))
                            match = true;
                    }
                }

                // Convert option values:
                // We cannot use a double as a option value; convert to integer
                // because we use IDs as option values
                try {

                    Double d = new Double(elementKey);
                    
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(0);
                    elementKey = nf.format(d);
                    
                }
                catch (NumberFormatException e) {
                }

                // combobox or listbox
                if (type.equals("combo") || type.equals("list")) {
                    output += "<option value=\"" + elementKey + "\"";
                    if (match)
                        output += " selected=\"selected\"";
                    output += ">" + val + "</option>\n";
                }

                //checkboxlist
                if (type.equals("checkbox")) {
                    output += "<input type=\"checkbox\" name=\""
                        + field.getName() + "\" value=\"" + elementKey + "\"";
                    if (match)
                        output += " checked=\"checked\"";
                    output += ">&nbsp;" + val + "<br/>\n";
                }

                //radiobuttonlist
                if (type.equals("radio")) {

                    if (!multiple) {
                        //normal list: you can choose one out of many
                        output += "<input type=\"radio\" name=\""
                            + field.getName() + "\" value=\"" + elementKey
                            + "\"";
                        if (match)
                            output += " checked=\"checked\"";
                        output += ">&nbsp;" + val + "\n";
                        //+ "<br/>\n";
                    }
                    else {

                        output += "<tr><td>";
                        //you can choose more than one out of many: display two
                        // radio buttons
                        output += "<input type=\"radio\" name=\""
                            + field.getName() + "_" + elementKey
                            + "\" value=\"true\"";
                        if (match)
                            output += " checked=\"checked\"";
                        output += ">";
                        output += "</td><td>";
                        output += "<input type=\"radio\" name=\""
                            + field.getName() + "_" + elementKey
                            + "\" value=\"false\"";
                        if (!match)
                            output += " checked=\"checked\"";
                        output += ">";
                        output += "</td><td>";
                        output += "&nbsp;" + val + "</td></tr>\n";

                    } //if multiple
                } //if radio

            } //while iterator

        } //if listelements not null

        //close tags, tags, or tables
        if (type.equals("combo") || type.equals("list")) {
            output += "</select>\n";
            output += getMarker();
        }
        else if (type.equals("radio")) {
            if (multiple)
                output += "</table>\n";
        } //if combo or radio

        return output;
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
     * getter for Options
     * 
     * @return Hasharray of strings
     */
    public LinkedHashMap getOptions() {
        return options;
    }

    /**
     * @param options
     */
    public void setOptions(LinkedHashMap options) {
        this.options = options;
    }

}