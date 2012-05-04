/*
 * Created on Jun 6, 2003
 *
 */
package com.wanci.dmerce.cli;

/**
 * @author rb
 * @version $Id: Cli.java,v 1.2 2004/03/24 17:43:13 rb Exp $
 *
 */
public interface Cli {

	/**
	 * Take command line arguments and construct a ArgumentParser
	 * @param args
	 * @return Instance of ArgumentParser
	 */
	ArgumentParser parseArguments();
	
}