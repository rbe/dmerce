/**
 * Created on Jan 20, 2003
 *
 */
package com.wanci.dmerce.kernel;

import com.wanci.java.UtilUtil;

/**
 * @author rb
 * @version $Id: Boot.java,v 1.6 2004/03/05 14:08:20 rb Exp $
 *  
 */
public class Boot {

	/**
	 */
	private static String copyright = "\n1[wan]Ci GmbH - http://www.1ci.com, dmerce(R)\nCopyright (C) 2000-2004\n";

	/**
	 */
	private static Runtime r = Runtime.getRuntime();

	/**
	 */
	private static String javaVendor = System.getProperty("java.vendor");

	/**
	 */
	private static String javaVersion = System.getProperty("java.version");

	/**
	 */
	private static String javaClassPath = System.getProperty("java.class.path");

	/**
	 */
	private static String javaExtDirs = System.getProperty("java.ext.dirs");

	/**
	 */
	private static String javaLibraryPath = System
			.getProperty("java.library.path");

	/**
	 */
	private static String javaRuntimeName = System
			.getProperty("java.runtime.name");

	/**
	 */
	private static String javaRuntimeVersion = System
			.getProperty("java.runtime.version");

	/**
	 */
	private static String javaVmVendor = System.getProperty("java.vm.vendor");

	/**
	 */
	private static String javaVmName = System.getProperty("java.vm.name");

	/**
	 */
	private static String javaVmVersion = System.getProperty("java.vm.version");

	/**
	 */
	private static String javaVmInfo = System.getProperty("java.vm.info");

	/**
	 */
	private static String javaIoTmpdir = System.getProperty("java.io.tmpdir");

	/**
	 */
	private static String osName = System.getProperty("os.name");

	/**
	 */
	private static String osVersion = System.getProperty("os.version");

	/**
	 */
	private static String osPatchlevel = System
			.getProperty("sun.os.patch.level");

	/**
	 */
	private static String osArch = System.getProperty("os.arch");

	/**
	 */
	private static String userName = System.getProperty("user.name");

	/**
	 */
	private static String userHome = System.getProperty("user.home");

	/**
	 */
	private static String userLanguage = System.getProperty("user.language");

	/**
	 * Method printCopyright.
	 */
	public static void printCopyright() {
		System.out.println(copyright);
	}

	/**
	 * Method printCopyright.
	 */
	public static void printCopyright(String s) {
		System.out.println(copyright);
		System.out.println(s + "\n");
	}

	/**
	 * Method printJavaEnvironment.
	 */
	public static void printJavaEnvironment() {
		System.out.println(javaVendor + " Java " + javaVersion);
		System.out.println(javaVmVendor + " " + javaVmName + " "
				+ javaVmVersion + " " + javaVmInfo);
		System.out.println(javaRuntimeName + " " + javaRuntimeVersion);
		System.out.println();
		System.out.println(osName + " " + osVersion + " Patchlevel: "
				+ osPatchlevel + " on " + osArch);
		System.out.println("# available processors to Java VM="
				+ r.availableProcessors());
		System.out.println("user.name=" + userName);
		System.out.println("user.home=" + userHome);
		System.out.println("user.language=" + userLanguage);
		System.out.println("java.class.path=" + javaClassPath);
		System.out.println("java.ext.dirs=" + javaExtDirs);
		System.out.println("java.library.path=" + javaLibraryPath);
		System.out.println("java.io.tmpdir=" + javaIoTmpdir);
	}

	/**
	 * @return Returns the copyright.
	 */
	public static String getCopyright() {
		return copyright;
	}

	/**
	 * @return Returns the javaClassPath.
	 */
	public static String getJavaClassPath() {
		return javaClassPath;
	}

	/**
	 * @return Returns the javaExtDirs.
	 */
	public static String getJavaExtDirs() {
		return javaExtDirs;
	}

	/**
	 * @return Returns the javaIoTmpdir.
	 */
	public static String getJavaIoTmpdir() {
		return javaIoTmpdir;
	}

	/**
	 * @return Returns the javaLibraryPath.
	 */
	public static String getJavaLibraryPath() {
		return javaLibraryPath;
	}

	/**
	 * @return Returns the javaRuntimeName.
	 */
	public static String getJavaRuntimeName() {
		return javaRuntimeName;
	}

	/**
	 * @return Returns the javaRuntimeVersion.
	 */
	public static String getJavaRuntimeVersion() {
		return javaRuntimeVersion;
	}

	/**
	 * @return Returns the javaVendor.
	 */
	public static String getJavaVendor() {
		return javaVendor;
	}

	/**
	 * @return Returns the javaVersion.
	 */
	public static String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * @return Returns the javaVmInfo.
	 */
	public static String getJavaVmInfo() {
		return javaVmInfo;
	}

	/**
	 * @return Returns the javaVmName.
	 */
	public static String getJavaVmName() {
		return javaVmName;
	}

	/**
	 * @return Returns the javaVmVendor.
	 */
	public static String getJavaVmVendor() {
		return javaVmVendor;
	}

	/**
	 * @return Returns the javaVmVersion.
	 */
	public static String getJavaVmVersion() {
		return javaVmVersion;
	}

	/**
	 * @return Returns the osArch.
	 */
	public static String getOsArch() {
		return osArch;
	}

	/**
	 * @return Returns the osName.
	 */
	public static String getOsName() {
		return osName;
	}

	/**
	 * @return Returns the osPatchlevel.
	 */
	public static String getOsPatchlevel() {
		return osPatchlevel;
	}

	/**
	 * @return Returns the osVersion.
	 */
	public static String getOsVersion() {
		return osVersion;
	}

	/**
	 * @return Returns the r.
	 */
	public static Runtime getR() {
		return r;
	}

	/**
	 * @return Returns the userHome.
	 */
	public static String getUserHome() {
		return userHome;
	}

	/**
	 * @return Returns the userLanguage.
	 */
	public static String getUserLanguage() {
		return userLanguage;
	}

	/**
	 * @return Returns the userName.
	 */
	public static String getUserName() {
		return userName;
	}

	public static void main(String[] args) {

		long time1 = System.currentTimeMillis();

		printCopyright();

		System.out.println("Dumping environment:\n");
		printJavaEnvironment();

		System.out.println();

		long time2 = System.currentTimeMillis();
		System.out.println((time2 - time1) + " milliseconds");

		UtilUtil.dumpProperties(System.getProperties());
		UtilUtil.dumpProperties(UtilUtil.filterPropertiesForKeys(
				new String[] { "" }, System.getProperties()));
	}

}