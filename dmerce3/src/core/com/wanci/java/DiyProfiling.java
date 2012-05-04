/*
 * Created on Jun 5, 2003
 *
 */
package com.wanci.java;

import java.util.HashMap;

/**
 * @author rb
 * @versioon $Id: DiyProfiling.java,v 1.1 2003/08/26 13:07:53 mm Exp $
 *
 * Do it yourself Profiling
 * 
 * Beispiel:
 * 
 * public void schlechtePerformance() {
 *
 * 		DyiProfiling dyip = new DyiProfiling("Ident");
 *  
 * 		dyip.addTimer("ersteAufgabe"); 
 * 		tueEinPaarEchtLangDauerndeDinger();
 * 		dyip.stopTimer("ersteAufgabe");
 * 
 * 		dyip.dump();
 * 
 * }
 * 
 */
public class DiyProfiling {

	/**
	 * 
	 */
	private String ident;

	/**
	 * 
	 */
	//private String subIdent;

	/**
	 * 
	 */
	private HashMap startTime = new HashMap();

	/**
	 * 
	 */
	private HashMap stopTime = new HashMap();

	/**
	 * 
	 * @param ident
	 */
	public DiyProfiling(String ident) {
		this.ident = ident;
	}

	/**
	 * 
	 *
	 */
	public void addTimer(String subIdent) {
		startTime.put(subIdent, new Long(System.currentTimeMillis()));
	}

	/**
	 * 
	 *
	 */
	public void dump() {
	}

	/**
	 * 
	 * @return
	 */
	public String getTimeElapsed(String subIdent) {

		return (
			((Long) stopTime.get(subIdent)).longValue()
				- ((Long) startTime.get(subIdent)).longValue())
			+ " ms";

	}

	/**
	 * 
	 *
	 */
	public void stopTimer(String subIdent) {
		stopTime.put(subIdent, new Long(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}

}