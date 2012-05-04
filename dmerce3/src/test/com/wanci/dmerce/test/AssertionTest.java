/*
 * Datei angelegt am 13.10.2003
 */
package com.wanci.dmerce.test;

/**
 * @author Masanori Fujita
 */
public class AssertionTest {
	
	public void someMethod(Object o) {
		assert o != null : "Parameter darf nicht null sein!";
		System.out.println(o.toString());
	}

	public static void main(String[] args) {
		AssertionTest at = new AssertionTest();
		at.someMethod("Hello world.");
		at.someMethod(null);
	}
}
