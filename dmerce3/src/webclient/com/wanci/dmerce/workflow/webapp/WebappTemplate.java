/*
 * Datei angelegt am 13.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

/**
 * Repräsentiert eine Template-Seite in einer Web-Anwendung.
 * Eine Template-Seite ist in der Regel eine JSP-Seite.
 * 
 * @author Masanori Fujita
 */
public class WebappTemplate {
	
	private String strTemplate;

	/**
	 * Konstruktor, der den Dateinamen der Template-Seite übergeben bekommt.  
	 * @param template Der Name der Template-Seite, die angezeigt werden soll.
	 * 		Diese darf nicht leer oder null sein.
	 */
	public WebappTemplate(String template) {
		assert template != null || template.equals("") : "Template darf nicht null oder leer sein.";
		this.strTemplate = template;
	}
	
	/**
	 * Konstruktor, der eine Dummy-Template-Seite erzeugt.
	 * Eine Dummy-Template-Seite verweist nicht auf eine JSP und kann
	 * von daher nicht angezeigt werden. Sie wird dazu verwendet, um
	 * die Hintereinanderschaltung von Transitionen zu erleichern.
	 */
	public WebappTemplate() {
		strTemplate = null;
	}
	
	/**
	 * Überprüft, ob dieses Template ein Dummy-Template ist.
	 * @return true, falls es sich um ein Dummy-Template handelt, false sonst
	 */
	public boolean isEmpty() {
		return strTemplate == null;
	}
	
	/**
	 * Gibt den Namen der Template-Seite als String zurück.
	 */
	public String getTemplate() {
		assert ! isEmpty() : "Von einem Dummy-Template kann keine Template-Seite bezogen werden.";
		return strTemplate;
	}

}
