/*
 * Created on Jun 9, 2003
 *  
 */
package com.wanci.dmerce.cli;

import com.wanci.dmerce.imageprocessing.ResizeImagesFromDatabase;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ResizeImages.java,v 1.3 2003/11/26 16:16:57 rb Exp $
 *  
 */
public class ResizeImages {

	public static void main(String[] args) {

		Boot.printCopyright("RESIZING IMAGES");

		LangUtil.consoleDebug(true, "START");

		ArgumentParser aap = new ArgumentParser(args);
		aap.add("-d", null);
		aap.parse();

		ResizeImagesFromDatabase ri = null;
		try {
			ri = new ResizeImagesFromDatabase();
			if (aap.hasArgument("-d"))
				ri.setDebug(true);
		}
		catch (Exception e) {
			LangUtil.consoleDebug(
				true,
				"Cannot find database connection"
					+ " ident 'qd_resizeimages' in dmerce.properties");
		}

		try {

			ri.openConnection();
			ri.go();
			ri.closeConnection();

		}
		catch (Exception e) {

			LangUtil.consoleDebug(
				true,
				"Unhandled exception: " + e.getCause() + ": " + e.getMessage());

			if (e.getCause() == null || e.getMessage() == null)
				e.printStackTrace();

		}

		LangUtil.consoleDebug(true, "STOP");

	}

}