/*
 * Datei angelegt am 01.10.2003
 */
package com.wanci.dmerce.kernel;

/**
 * @author Masanori Fujita
 */
public interface XmlConfigReaderInterface {
	/**
	 * resource string for xml string
	 */
	public void setXmlResource(String string);

	/**
	 * read the xml resource
	 * @return true/false
	 */
	public boolean read();

}