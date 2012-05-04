/*
 * Created on 25.07.2003
 *
 */
package com.wanci.dmerce.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StreamTokenizer;
import java.math.BigInteger;

/**
 * @author rb
 * @version $Id: CsvToXml.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * Erzeugt aus einer CSV-Datei:
 * 
 * ID;Name
 * 1;Ralf
 * 
 * folgendes XML Format:
 * 
 * <document>
 * 
 * <header>
 * <column position="1">Name des Headers</column>
 * </header>
 * 
 * <row number="1">
 * <column position="1" name="ID">DATA</column>
 * <column position="2" name="Name">DATA</column>
 * </row>
 * 
 * </document>
 *
 */
public class CsvToXml {

	public void csvToXML(String inputFile, String outputFile)
		throws java.io.FileNotFoundException, java.io.IOException {

		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		StreamTokenizer st = new StreamTokenizer(br);
		// We want to read the file one line at a time, so end-ofline matters
		st.eolIsSignificant(true);
		// The delimiter between fields is a comma, not a space
		st.whitespaceChars(',', ',');
		// All strings are in double quotes
		st.quoteChar('"');

		FileWriter fw = new FileWriter(outputFile);
		// Write the XML declaration and the root element
		fw.write("<?xml version=\"1.0\"?>\n");
		fw.write("<document>\n");

		// Get the first token, then check its type
		st.nextToken();
		while (st.ttype != StreamTokenizer.TT_EOF) {
			// We're not at EOF, so start a row
			fw.write("  <row>\n");
			int i = 1;
			while (st.ttype != StreamTokenizer.TT_EOL
				&& st.ttype != StreamTokenizer.TT_EOF) {
				// We use the BigInteger class here to write long numbers out
				// Without this, the date fields (which are written something 
				// like (19991013) get converted to scientific notation....
				if (st.ttype == StreamTokenizer.TT_NUMBER) {
					fw.write("    <column" + i + ">");
					fw.write((BigInteger.valueOf((long) st.nval)).toString());
					fw.write("</column" + i++ +">\n");
				}
				else if (
					st.ttype != StreamTokenizer.TT_EOL
						&& st.ttype != StreamTokenizer.TT_EOF) {
					// For reasons that escape me, if the token is "+", it
					// is interpreted as NULL.
					if (st.sval != null) {
						fw.write("    <column" + i + ">");
						fw.write(st.sval.trim());
						fw.write("</column" + i++ +">\n");
					}
				}
				st.nextToken();
			}

			// We've hit either the end of the line or the end of the file,
			// so close the row.
			fw.write("  </row>\n");
			st.nextToken();
		}

		// Now we're at the end of the file, so close the XML document, 
		// flush the buffer to disk, and close the newly-created file. 
		fw.write("</document>\n");
		fw.flush();
		fw.close();

	}

	public static void usage() {
		System.out.println("\nUsage: java CsvToXml csv-file xml-file");
		System.out.println(
			"       where csv-file is the comma-separated file, and ");
		System.out.println("       xml-file is the XML file to be generated.");
	}

	public static void main(String args[]) throws java.io.IOException {

		if (args.length == 2) {
			CsvToXml cp = new CsvToXml();
			cp.csvToXML(args[0], args[1]);
		}
		else
			usage();

	}

}