/*
 * Created on Oct 20, 2003
 *
 * Datei dient nur noch dazu um den Code zu halten 
 *
 */
package com.wanci.dmerce.test;

import com.wanci.dmerce.dbmgr.SplashScreen;

/**
 * @author tw
 * 
 * @ version $id$
 *
 */

public class ThreadTest extends Thread {

	long minPrime;

	ThreadTest(long minPrime) {
		this.minPrime = minPrime;
	}

	public void run() {

		new SplashScreen(
			"test/1ci_splashscreen.jpg",
			"(C) Copyright 2000-2003 1Ci GmbH Münster, All Rights Reserved");

	}

}