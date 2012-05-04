/*
 * DmerceProperties.java
 *
 * Created on January 6, 2003, 2:05 PM
 */

package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.MySQL;
import com.wanci.dmerce.jdbcal.Pointbase;
import com.wanci.dmerce.jdbcal.PostgreSQL;
import com.wanci.java.LangUtil;
import com.wanci.java.UtilUtil;

/** Liest, verwaltet und lässt die dmerce.properties abfragen.
 *
 * @author  rb
 */
public class DmerceProperties {

	/**
	 * singleton design pattern variable
	 * @author pg
	 */
	private static DmerceProperties object = null;

	private FileInputStream fin;

	public String[] loadedFiles;

	private Properties sysProps = System.getProperties();

	private Properties dmerceProps;

	//private Properties dmerceSysProps;

	private Properties dmerceNccProps;

	private String nccMonitorInetServicePrefix = "ncc.monitor.inet";

	private String[] filenames =
		new String[] {
			File.separatorChar
				+ "etc"
				+ File.separatorChar
				+ "dmerce.properties",
			sysProps.getProperty("user.home")
				+ File.separatorChar
				+ "dmerce.properties",
			sysProps.getProperty("user.dir")
				+ File.separatorChar
				+ "etc"
				+ File.separatorChar
				+ "dmerce.properties" };

	/**
	 * singleton pattern
	 * @return object
	 */
	public static synchronized DmerceProperties getInstance() {
		if (object == null) {
			object = new DmerceProperties();
		}
		return object;
	}

	/** 
	 * Creates a new instance of DmerceProperties 
	 */
	public DmerceProperties() {
		dmerceProps = new Properties();
		loadedFiles = loadFromFiles();
		//dmerceSysProps = getSysProperties();
		dmerceNccProps = getNccProperties();
	}

	/** Liefert eine Instanz einer JDBC Klasse zurück, die die Verbindung
	 * zur einer Datenbank darstellt. Je nach Werten in der dmerce.properties
	 * 
	 * @return Objekt vom Typ JdbcDatabase (Interface aller Jdbc*-Klassen)
	 */
	public Database getDatabaseConnection(String jdbcPrefix) throws DmerceException {
		Database jd = null;
		String prefix = jdbcPrefix + ".jdbc";
		String jdbcUrl = dmerceProps.getProperty(prefix + ".url");
		String jdbcUsername = dmerceProps.getProperty(prefix + ".username");
		String jdbcPassword = dmerceProps.getProperty(prefix + ".password");

		if (jdbcUrl.indexOf("mysql") > 0)
			jd = new MySQL(jdbcUrl, jdbcUsername, jdbcPassword);
		else if (jdbcUrl.indexOf("postgresql") > 0)
			jd = new PostgreSQL(jdbcUrl, jdbcUsername, jdbcPassword);
		//else if (jdbcUrl.indexOf("oracle") > 0)
		//	jd = new JdbcOracle(jdbcUrl, jdbcUsername, jdbcPassword);
		else if (jdbcUrl.indexOf("pointbase") > 0)
			jd = new Pointbase(jdbcUrl, jdbcUsername, jdbcPassword);
		return (Database) jd;
	}

	/**
	 * Method getDmerceProperties.
	 * @return Properties
	 */
	public Properties getDmerceProperties() {
		return dmerceProps;
	}

	/** Liefert ein neues Properties-Objekt mit allen Lizenz-Parametern
	 * zurück (sys.license)
	 *
	 * @return Properties
	 */
	public Properties getLicenseProperties() {
		return UtilUtil.filterPropertiesForKeys(
			new String[] { "sys.license" },
			dmerceProps);
	}

	/** Liefert ein neues Properties-Objekt mit allen NCC-Parametern
	 * zurück
	 *
	 * @return Properties
	 */
	public Properties getNccProperties() {
		return UtilUtil.filterPropertiesForKeys(
			new String[] { "ncc" },
			dmerceProps);
	}

	/** Liefert eine Hashmap der NCC Monitoring Farbwerte zu ID
	 */
	public HashMap getNccMonitorInetColors() {
		HashMap m = new HashMap();
		Properties p =
			UtilUtil.filterPropertiesForKeys(
				new String[] { "ncc.monitor.inet.color" },
				dmerceNccProps);
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String k = key.replaceAll("ncc.monitor.inet.color.", "");
			k = k.replaceAll(".id", "");
			m.put(k.toLowerCase(), new Integer(p.getProperty(key)));
		}
		return m;
	}

	/**
	 * Method getNccMonitorInetServers.
	 * @return Vector
	 */
	public Iterator getNccMonitorInetServers() {
		return LangUtil
			.stringTokensToVector(
				new StringTokenizer(
					dmerceNccProps.getProperty("ncc.monitor.inet.servers"),
					","))
			.iterator();
	}

	/**
	 * Method getNccMonitorInetServerId.
	 * @param host
	 * @return int
	 */
	public int getNccMonitorInetServerId(String host) {
		return new Integer(
			dmerceNccProps.getProperty("ncc.monitor.inet." + host + ".id"))
			.intValue();
	}

	/**
	 * Method getNccMonitorInetServiceId.
	 * @param host
	 * @return int
	 */
	public int getNccMonitorInetServiceId(String service) {
		return new Integer(
			dmerceNccProps.getProperty(
				"ncc.monitor.inet.service." + service + ".id"))
			.intValue();
	}

	/**
	 * Method getNccMonitorInetServerServices.
	 * @param host
	 * @return Vector
	 */
	public Iterator getNccMonitorInetServerServices(String host) {
		return LangUtil
			.stringTokensToVector(
				new StringTokenizer(
					dmerceNccProps.getProperty(
						"ncc.monitor.inet." + host + ".services"),
					","))
			.iterator();
	}

	public Iterator getNccMonitorInetServerServicePorts(
		String host,
		String service) {
		return LangUtil
			.stringTokensToVector(
				new StringTokenizer(
					getNccMonitorInetService(host, service, "ports"),
					","))
			.iterator();

	}

	/** Liefert einen Wert einer NCC-Monitoring-InetService-Property
	 *
	 * @param host Hostname
	 * @param service das gewünschte InetService
	 * @param suffix das Suffix (ports, send, expect)
	 * @return String; der Wert der gewünschten NCC-Property
	 */
	public String getNccMonitorInetService(
		String host,
		String service,
		String suffix) {
		String key =
			nccMonitorInetServicePrefix
				+ "."
				+ host
				+ "."
				+ service
				+ "."
				+ suffix;
		Properties ports =
			UtilUtil.filterPropertiesForKeys(
				new String[] { key },
				dmerceNccProps);
		return ports.getProperty(key);
	}

	/** Liefert ein neues Properties-Objekt mit allen sys-Parametern
	 * zurück
	 *
	 * @return Properties
	 */
	public Properties getSysProperties() {
		return UtilUtil.filterPropertiesForKeys(
			new String[] { "sys" },
			dmerceProps);
	}

	/** Sucht die dmerce.properties in den Verzeichnissen:
	 * /etc
	 * $DMERCE_HOME
	 * und lädt alle die gefunden werden
	 *
	 * @return String[] mit Liste aller geladenen Properties-Dateien
	 */
	public String[] loadFromFiles() {
		String[] loadedFiles = new String[] {
		};
		for (int i = 0; i < filenames.length; i++) {
			try {
				fin = new FileInputStream(new File(filenames[i]));
				dmerceProps.load(fin);
				fin.close();
				String[] l = new String[loadedFiles.length + 1];
				System.arraycopy(loadedFiles, 0, l, 0, loadedFiles.length);
				l[l.length - 1] = filenames[i];
				loadedFiles = l;
			}
			catch (FileNotFoundException fne) {
				//System.out.println("Did not find file: " + filenames[i]);
			}
			catch (IOException ioe) {
				//System.out.println(ioe.toString());
			}
		}
		return loadedFiles;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		DmerceProperties d = new DmerceProperties();
		System.out.print("Loading dmerce.properties from:");
		String[] lf = d.loadedFiles;
		if (lf.length > 0) {
			for (int i = 0; i < lf.length; i++)
				System.out.print(" " + lf[i]);
			System.out.println("\n\nDumping all properties");
			UtilUtil.dumpProperties(d.getDmerceProperties());
			System.out.println("\n\nDumping Sys properties");
			UtilUtil.dumpProperties(d.getSysProperties());
			System.out.println("\n\nDumping NCC properties");
			UtilUtil.dumpProperties(d.getNccProperties());
			System.out.println(
				"-->"
					+ d.getNccMonitorInetService("localhost", "http", "ports"));
			d.getNccMonitorInetServers();
			System.out.println("\n\nDumping System.properties:");
			UtilUtil.dumpProperties(System.getProperties());
		}
		else {
			System.out.println("No dmerce.properties found!");
		}
	}

}