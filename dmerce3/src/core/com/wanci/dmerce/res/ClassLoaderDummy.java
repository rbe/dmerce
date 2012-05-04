/*
 * Datei angelegt am 18.11.2003
 */
package com.wanci.dmerce.res;

/**
 * Dies ist eine Dummy-Klasse, die lediglich dazu dient,
 * aus einem anderen ClassLoader heraus auf den ClassLoader
 * dieser Klasse zuzugreifen.<p/>
 * Dies ist sinnvoll im Zusammenhang mit dem XmlDocumentCache,
 * der manchmal Probleme hat, die forms.xml und die workflow.xml
 * auszulesen, weil er aus einem anderen ClassLoader heraus geladen
 * wird.
 * 
 * @author Masanori Fujita
 */
public class ClassLoaderDummy {
	// leer lassen!
}
