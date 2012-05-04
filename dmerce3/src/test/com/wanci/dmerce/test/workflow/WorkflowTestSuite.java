/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.test.workflow;



import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Masanori Fujita
 */
public class WorkflowTestSuite {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(WorkflowTestSuite.class);
	}

	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for com.wanci.dmerce.test.workflow");
		//$JUnit-BEGIN$
		suite.addTestSuite(WorkflowTest.class);
		suite.addTestSuite(StateTest.class);
		suite.addTestSuite(TransitionTest.class);
		suite.addTestSuite(ConditionTest.class);
		suite.addTestSuite(ActionTest.class);
		suite.addTestSuite(TransitionFactoryTest.class);
		//$JUnit-END$
		return suite;
	}
	
}
