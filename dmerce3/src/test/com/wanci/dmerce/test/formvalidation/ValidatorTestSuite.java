/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.test.formvalidation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Masanori Fujita
 */
public class ValidatorTestSuite {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(ValidatorTestSuite.class);
	}

	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for com.wanci.dmerce.test.formvalidation");
		//$JUnit-BEGIN$
		suite.addTestSuite(EmailValidatorTest.class);
		suite.addTestSuite(FormatValidatorTest.class);
		suite.addTestSuite(RequiredValidatorTest.class);
		suite.addTestSuite(MaxInclusiveValidatorTest.class);
		suite.addTestSuite(MinInclusiveValidatorTest.class);
		suite.addTestSuite(MaxLengthValidatorTest.class);
		suite.addTestSuite(MinLengthValidatorTest.class);
		suite.addTestSuite(NumberValidatorTest.class);
		suite.addTestSuite(ScaleValidatorTest.class);
		//$JUnit-END$
		return suite;
	}
}
