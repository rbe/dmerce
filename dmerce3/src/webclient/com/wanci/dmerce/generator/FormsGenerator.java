/*
 * Created on Mar 18, 2004
 *
 */
package com.wanci.dmerce.generator;

import com.wanci.dmerce.jdbcal.Database;

/**
 * @author rb2
 * @version $$Id: FormsGenerator.java,v 1.1 2004/03/19 14:25:18 rb Exp $$
 *  
 */
public class FormsGenerator {

	private Database jdbc;
	
	private Form[] forms;

	FormsGenerator(Database jdbc) {
		this.jdbc = jdbc;
	}

}