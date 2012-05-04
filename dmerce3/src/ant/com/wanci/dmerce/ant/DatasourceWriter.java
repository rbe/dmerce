/*
 * Datei angelegt am 01.03.2004
 */
package com.wanci.dmerce.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * DatasourceWriter
 * 
 * @author Masanori Fujita
 */
public class DatasourceWriter {

	private Document properties_xml;

	private File datasource_file;

	private String appname;

	private XMLOutputter outputter;

	public DatasourceWriter(String appname, File properties, File datasource) throws JDOMException,
			IOException {

		datasource_file = datasource;
		this.appname = appname;

		// XML-Dateien als JDOM-Dokumente auslesen
		SAXBuilder builder = new SAXBuilder();
		FileInputStream inputStream;

		inputStream = new FileInputStream(properties);
		properties_xml = builder.build(inputStream);
		inputStream.close();

		outputter = new XMLOutputter();
		outputter.setFormat(org.jdom.output.Format.getPrettyFormat());

	}

	/**
	 * Gibt den Wert eines Property-Eintrages aus der properties.xml zurück.
	 * 
	 * @param name Name der Property
	 * @return Wert der Property
	 */
	private String getProperty(String name) {
		Element root = properties_xml.getRootElement();
		Iterator it = root.getChildren().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.getAttribute("name").getValue().equals(name)) {
				return element.getAttribute("value").getValue();
			}
		}
		return null;
	}

	private void writeDatasourceFile(Document doc) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(datasource_file);
		outputter.output(doc, outputStream);
		outputStream.close();
	}

	private void createOracleDatasource(String jdbcUrl, String username, String password)
			throws IOException {
		Element jndiName = new Element("jndi-name");
		jndiName.setText(NamingConventions.getDatasourceJndiNameForAppname(appname));
		Element connectionUrl = new Element("connection-url");
		connectionUrl.setText(jdbcUrl);
		Element driverClass = new Element("driver-class");
		driverClass.setText("oracle.jdbc.driver.OracleDriver");
		Element usernameEl = new Element("user-name");
		usernameEl.setText(username);
		Element passwordEl = new Element("password");
		passwordEl.setText(password);
		Element exceptionSorter = new Element("exception-sorter-class-name");
		exceptionSorter.setText("org.jboss.resource.adapter.jdbc.vendor.OracleExceptionSorter");
		Element txdatasource = new Element("local-tx-datasource");
		txdatasource.addContent(jndiName);
		txdatasource.addContent(connectionUrl);
		txdatasource.addContent(driverClass);
		txdatasource.addContent(usernameEl);
		txdatasource.addContent(passwordEl);
		txdatasource.addContent(exceptionSorter);
		Element root = new Element("datasources");
		root.addContent(txdatasource);
		Document ds = new Document(root);
		writeDatasourceFile(ds);
	}

	private void createSimpleDatasource(String driverClassName, String jdbcUrl, String username,
			String password) throws IOException {
		Element jndiName = new Element("jndi-name");
		jndiName.setText(NamingConventions.getDatasourceJndiNameForAppname(appname));
		Element connectionUrl = new Element("connection-url");
		connectionUrl.setText(jdbcUrl);
		Element driverClass = new Element("driver-class");
		driverClass.setText(driverClassName);
		Element usernameEl = new Element("user-name");
		usernameEl.setText(username);
		Element passwordEl = new Element("password");
		passwordEl.setText(password);
		Element txdatasource = new Element("local-tx-datasource");
		txdatasource.addContent(jndiName);
		txdatasource.addContent(connectionUrl);
		txdatasource.addContent(driverClass);
		txdatasource.addContent(usernameEl);
		txdatasource.addContent(passwordEl);
		Element root = new Element("datasources");
		root.addContent(txdatasource);
		Document ds = new Document(root);
		writeDatasourceFile(ds);
	}

	public void createDatasourceFile() throws Exception {
		String url;
		url = getProperty(appname + ".jdbc.url");
		if (url == null) {
			url = getProperty("default.jdbc.url");
		}
		if (url == null) throw new Exception("*.jdbc.url not found among properties.");
		String username;
		username = getProperty(appname + ".jdbc.username");
		if (username == null) {
			username = getProperty("default.jdbc.username");
		}
		if (username == null) throw new Exception("*.jdbc.username not found among properties.");
		String password;
		password = getProperty(appname + ".jdbc.password");
		if (password == null) {
			password = getProperty("default.jdbc.password");
		}
		if (password == null) throw new Exception("*.jdbc.password not found among properties.");
		String driverClass;
		driverClass = getProperty(appname + ".jdbc.driver");
		if (driverClass == null) {
			driverClass = getProperty("default.jdbc.driver");
		}
		if (driverClass == null) throw new Exception("*.jdbc.driverClass not found among properties.");
		// Datenbanktyp erkennen
		if (url.indexOf("mysql") > -1) {
			createSimpleDatasource(driverClass, url, username, password);
		} else if (url.indexOf("postgresql") > -1) {
			createSimpleDatasource(driverClass, url, username, password);
		} else if (url.indexOf("oracle") > -1) {
			createOracleDatasource(url, username, password);
		} else {
			throw new Exception("Datenbanktyp unbekannt.");
		}
	}

	public static void main(String[] args) {
		String appname = "dmerce-test";
		File properties = new File("C:\\Temp\\test\\properties.xml");
		File datasource = new File("C:\\Temp\\test\\" + appname + "-ds.xml");
		try {
			DatasourceWriter instance = new DatasourceWriter(appname, properties, datasource);
			instance.createDatasourceFile();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
