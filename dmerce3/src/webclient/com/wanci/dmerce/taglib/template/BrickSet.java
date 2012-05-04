/*
 * Datei angelegt am 21.11.2003
 */
package com.wanci.dmerce.taglib.template;

import java.util.HashMap;

/**
 * @author Masanori Fujita
 */
public class BrickSet {
	
	private HashMap templates;
	
	public BrickSet() {
		this.templates = new HashMap();
	}
	
	public void addTemplate(String key, String template) {
		templates.put(key, template);
	}
	
	public String getTemplate(String key) {
		return (String) templates.get(key);
	}
	
	
}
