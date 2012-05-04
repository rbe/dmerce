/*
 * Datei angelegt am 18.11.2003
 */
package com.wanci.dmerce.test;

import com.wanci.dmerce.servlet.XmlDocumentCache;

/**
 * @author Masanori Fujita
 */
public class ClassLoaderTest {

	public static void main(String[] args) {
		ClassLoaderTest instance = new ClassLoaderTest();
		System.out.println(instance.getClass().getClassLoader().toString());
		System.out.println(XmlDocumentCache.class.getClassLoader().toString());
	}
}
