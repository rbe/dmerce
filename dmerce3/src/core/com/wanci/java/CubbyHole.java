package com.wanci.java;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CubbyHole {

	/**
	 */
	private int contents;

	/**
	 */
	private boolean available = false;

	/**
	 * Method get.
	 * @return int
	 */
	public synchronized int get() {
		while (available == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		available = false;
		notifyAll();
		return contents;
	}

	/**
	 * Method put.
	 * @param value
	 */
	public synchronized void put(int value) {
		while (available == true) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		contents = value;
		available = true;
		notifyAll();
	}

}