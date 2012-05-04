/*
 * Datei angelegt am 02.02.2004
 */
package com.wanci.dmerce.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.taglib.form.FormTag;
import com.wanci.dmerce.taglib.sql.RowTag;
import com.wanci.java.LangUtil;

/**
 * FileLinkTag erzeugt einen Link zu einer zuvor hochgeladenen Datei. Zwei
 * zulässige Anwendungsmöglichkeiten: - Das UploadResTag wird mit einem
 * FormField verbunden, das Datei-Informationen enthält. (Innerhalb eines
 * <form>-Tags) - Das UploadResTag wird mit einem QResult-Field verbunden, das
 * Datei-Informationen enthält. (Innerhalb eines <execute>-Tags)
 * 
 * @author Masanori Fujita
 */
public class UploadResTag extends BodyTagSupport {

	private boolean DEBUG = false;
	private boolean DEBUG2 = false;

	private String name;
	private String emptypath = "";
	private String SUFFIX_ORIGINAL;
	private String SUFFIX_SERVER;

	public UploadResTag() throws JspException, XmlPropertiesFormatException {

		DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean("debug");

		try {
			SUFFIX_ORIGINAL =
				XmlPropertiesReader.getInstance().getProperty(
					"fileupload.columnsuffix.originalfile");
			SUFFIX_SERVER =
				XmlPropertiesReader.getInstance().getProperty(
					"fileupload.columnsuffix.serverfile");
		}
		catch (XmlPropertiesFormatException e) {
			e.printStackTrace();
			throw new JspException(e);
		}

	}

	public int doStartTag() throws JspException {

		FormTag form;
		form = (FormTag) findAncestorWithClass(this, FormTag.class);
		FormField f;

		// Innerhalb eines Form-Tags?
		if (form != null) {

			try {
				f = form.getField(name);
			}
			catch (FieldNotFoundException e) {

				String message = e.getMessage();
				String jspPath =
					((HttpServletRequest) pageContext.getRequest())
						.getRequestURI();

				message += "Could not find field '"
					+ name
					+ "'! Please check &lt;qform:uploadres&gt;-tags"
					+ " in "
					+ jspPath;
				LangUtil.consoleDebug(DEBUG, message);

				JspException jspe = new JspException("<br>" + message + "<br>");
				jspe.setStackTrace(e.getStackTrace());
				throw jspe;

			}
			try {

				String contextPath =
					((HttpServletRequest) pageContext.getRequest())
						.getContextPath();

				if (f.getValues() != null & !f.getValue().equals("")) {
					String path =
						contextPath
							+ "/files/"
							+ f.getValues()[1]
							+ "?file="
							+ f.getValues()[0];
					pageContext.getOut().print(path);
				}
				else {
					String path = contextPath;
					if (emptypath == null || emptypath.equals("")) {
						path = "";
					}
					else {
						if (emptypath.charAt(0) != '/') {
							path += "/";
						}
						path += emptypath;
					}
					pageContext.getOut().print(path);
				}

			}
			catch (IOException e) {
				throw new JspException(e);
			}

			return SKIP_BODY;

		}

		// Oder doch innerhalb eines SQL-Tags?
		RowTag row;
		row = (RowTag) findAncestorWithClass(this, RowTag.class);
		if (row != null) {

			String originalFileName = name + SUFFIX_ORIGINAL;
			String serverFileName = name + SUFFIX_SERVER;
			Object o = row.getRow().getValue(originalFileName);
			Object s = row.getRow().getValue(serverFileName);
			LangUtil.consoleDebug(
				DEBUG2,
				this,
				"original filename:"
					+ originalFileName
					+ ", server filename:"
					+ serverFileName);

			String contextPath =
				((HttpServletRequest) pageContext.getRequest())
					.getContextPath();
			
			if (!o.equals("") & !s.equals("")) {
				
				String path = contextPath + "/files/" + o + "?file=" + s;
				try {
					pageContext.getOut().print(path);
				}
				catch (IOException e) {
					throw new JspException(e);
				}
				
			}
			else {
				
				String path = contextPath;
				if (emptypath == null || emptypath.equals("")) {
					path = "";
				}
				else {
					if (emptypath.charAt(0) != '/') {
						path += "/";
					}
					path += emptypath;
				}
				
				try {
					pageContext.getOut().print(path);
				}
				catch (IOException e) {
					throw new JspException(e);
				}

			}
			return SKIP_BODY;
		}

		throw new JspException(
			"You have to use <q:uploadres> either within a <qform>"
				+ " or a <qsql:execute> tag");

	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmptypath(String path) {
		this.emptypath = path;
	}
}
