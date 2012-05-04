/**
 * Created on 18.01.2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.servicemonitor;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class InetServices {

	public static InetServiceDefinition ftp =
		new InetServiceDefinition(
			"FTP",
			"File Transfer Protocol",
			new int[] { 20, 21 },
			new int[] {
	});

	public static InetServiceDefinition telnet =
		new InetServiceDefinition(
			"Telnet",
			"Telnet",
			new int[] { 23 },
			new int[] {
	});

	public static InetServiceDefinition smtp =
		new InetServiceDefinition(
			"SMTP",
			"Simple Mail Transfer Protocol",
			new int[] { 25 },
			new int[] {
	});

	public static InetServiceDefinition http =
		new InetServiceDefinition(
			"HTTP",
			"Hypertext Transfer Protocol",
			new int[] { 80 },
			new int[] {
	});

	public static InetServiceDefinition https =
		new InetServiceDefinition(
			"HTTPS",
			"Hypertext Transfer Protocol w/ SSL",
			new int[] { 443 },
			new int[] {
	});

	public static InetServiceDefinition pop3 =
		new InetServiceDefinition(
			"POP3",
			"Post Office Protocol",
			new int[] { 110 },
			new int[] {
	});

	public static InetServiceDefinition imap4 =
		new InetServiceDefinition(
			"IMAP4",
			"Internet Mail A Protocol",
			new int[] { 143 },
			new int[] {
	});

}