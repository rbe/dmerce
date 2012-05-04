/*
 * Datei angelegt am 02.03.2004
 */
package com.wanci.dmerce.ant;

/**
 * NamingConventions
 * 
 * @author Masanori Fujita
 */
public class NamingConventions {

	/**
	 * Gibt den JNDI-Namen (ohne JNDI-Prefixes) für ein Datasource zu einem bestimmten
	 * Anwendungsnamen zurück.
	 */
	public static String getDatasourceJndiNameForAppname(String appname) {
		return appname + "DS";
	}

	/**
	 * Gibt den vollen JNDI-Pfad für ein Datasource zu einem bestimmten Anwendungsnamen zurück.
	 */
	public static String getDatasourceJndiLinkForAppname(String appname) {
		return "java:/" + getDatasourceJndiNameForAppname(appname);
	}
	
	public static String getSecuredAreaPathForWorkflow(String workflowid) {
		return "/_sec_/"+workflowid;
	}
	
	public static String getRelativeSecuredAreaPathForWorkflow(String workflowid) {
		return getSecuredAreaPathForWorkflow(workflowid).substring(1);
	}

}
