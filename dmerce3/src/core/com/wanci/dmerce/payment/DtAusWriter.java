/*
 * Created on 09.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author rb
 * @version ${Id}
 * 
 * Write DTAUS file
 *  
 */
public class DtAusWriter {

	/**
	 *  
	 */
	private File file;

	/**
	 * Dataset A
	 */
	private DtAusSetA dtAusSetA;

	/**
	 * Vector holding all C datasets
	 */
	private Vector dtAusSetC = new Vector();

	/**
	 * Dataset E
	 */
	private DtAusSetE dtAusSetE;

	/**
	 * Contructor
	 * 
	 * @param file
	 */
	public DtAusWriter(File file) {
		this.file = file;
	}

	/**
	 * Set A dataset
	 * 
	 * @param dtAusSetA
	 */
	public void setA(DtAusSetA dtAusSetA) {
		this.dtAusSetA = dtAusSetA;
	}

	/**
	 * Add another C dataset
	 * 
	 * @param dtAusSetC
	 */
	public void addC(DtAusSetC dtAusSetC) {
		this.dtAusSetC.add(dtAusSetC);
	}

	/**
	 * Iterator over all C datasets
	 * 
	 * @return
	 */
	public Iterator getIterator() {
		return dtAusSetC.iterator();
	}

	/**
	 * Set dataset E
	 * 
	 * @param dtAusSetE
	 */
	public void setE(DtAusSetE dtAusSetE) {
		this.dtAusSetE = dtAusSetE;
	}

	/**
	 * Write all datasets to 'file'
	 *  
	 */
	public void writeFile() throws IOException {

		FileWriter fw = new FileWriter(file);
		fw.write(dtAusSetA.toString());

		Iterator i = dtAusSetC.iterator();
		while (i.hasNext()) {

			String c = ((DtAusSetC) i.next()).toString();
			fw.write(c);

		}

		fw.write(dtAusSetE.toString());
		fw.close();

	}

}