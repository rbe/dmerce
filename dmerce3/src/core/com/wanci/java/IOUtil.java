/*
 * Created on May 30, 2003
 *  
 */
package com.wanci.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author rb
 * @version $Id: IOUtil.java,v 1.5 2004/04/15 12:01:07 rb Exp $
 *  
 */
public class IOUtil {

	public static final int LEFT = 1;

	public static final int RIGHT = 2;

	/** You cannot create an instance */
	private IOUtil() {
	}

	/**
	 * @param inFileName
	 * @param outFileName
	 * @throws IOException
	 */
	public static void copyFile(String inFileName, String outFileName)
			throws IOException {

		File inputFile = new File(inFileName);
		File outputFile = new File(outFileName);

		FileReader in = new FileReader(inputFile);
		FileWriter out = new FileWriter(outputFile);
		int c;

		while ((c = in.read()) != -1)
			out.write(c);

		in.close();
		out.close();

	}

	/**
	 * @param input
	 * @param orientation
	 * @param length
	 * @param fillWith
	 * @return
	 */
	public static String justify(String input, int orientation, int length,
			String fillWith) {

		String output = input;
		int lengthOfInput = input.length();
		int lengthDelta = length - lengthOfInput;

		String fill = "";
		if (lengthDelta > 0) {

			for (int i = 0; i < lengthDelta; i++)
				fill += fillWith;

			if (orientation == LEFT)
				output += fill;
			else if (orientation == RIGHT)
				output = fill + input;

		}
		else
			output = input.substring(0, length);

		return output;

	}

	/**
	 * Write contents of a buffer into a file
	 * 
	 * @param br
	 * @param f
	 * @throws IOException
	 */
	public static void writeBufferToFile(BufferedReader br, File f)
			throws IOException {

		String line;
		FileWriter fw = new FileWriter(f);

		while ((line = br.readLine()) != null) {
			fw.write(line + "\n");
		}
		
		fw.close();

	}

}