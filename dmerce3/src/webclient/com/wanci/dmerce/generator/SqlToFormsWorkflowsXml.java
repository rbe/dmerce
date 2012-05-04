/*
 * Created on 14.03.2004
 *  
 */
package com.wanci.dmerce.generator;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author rb
 * @version $Id: SqlToFormsWorkflowsXml.java,v 1.2 2004/03/19 14:25:28 rb Exp $
 * 
 * Generiert eine forms und workflows.xml anhand eines Datenbankschema
 * (Tabellen)
 *  
 */
public class SqlToFormsWorkflowsXml {
	
	public static void main(String[] args) {
		
		Database jdbc = DatabaseHandler.getDatabaseConnection("");
		
		// Bildet Tabellen in Formulare ab
		FormsGenerator fg = new FormsGenerator(jdbc);
		
		// Erzeugt Workflows zu den jeweiligen Formularen
		WorkflowsGenerator wg = new WorkflowsGenerator(fg);
		
		// Erzeuge JSPs fuer die Workflows
		JspGenerator jg = new JspGenerator(wg);
		
	}

}