/*
 * Datei angelegt am 26.02.2004
 */
package com.wanci.dmerce.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Der SecurityConfigWriter erzeugt aus der security.xml einer dmerce-Anwendung die notwendigen
 * Einträge in der login-config.xml von JBoss und der web.xml der Webanwendung.
 * 
 * @author Masanori Fujita
 */
public class SecurityConfigWriter {

	public static final int LOGIN_DATABASE = 1;

	private Document security_xml;

	private Document login_config_xml;

	private Document web_xml;

	private File login_file;

	private File web_file;

	private XMLOutputter outputter;

	private String appname;

	private File jbossweb_file;

	private class MissConfigurationException extends Exception {

		public MissConfigurationException(String message) {
			super(message);
		}
	}

	public SecurityConfigWriter(String appname, File security, File login, File web, File jbossweb)
			throws JDOMException, IOException {

		this.appname = appname;

		login_file = login;
		web_file = web;
		jbossweb_file = jbossweb;

		// Validierung
		// TODO: Hier die Validierung der XML-Dateien vornehmen
		// Es gab allerdings Probleme, XML-Schemas zur Validierung zu verwenden.
		
		// XML-Dateien als JDOM-Dokumente auslesen
		SAXBuilder builder = new SAXBuilder();
		FileInputStream inputStream;

		inputStream = new FileInputStream(security);
		security_xml = builder.build(inputStream);
		inputStream.close();

		inputStream = new FileInputStream(login);
		login_config_xml = builder.build(inputStream);
		inputStream.close();

		inputStream = new FileInputStream(web);
		web_xml = builder.build(inputStream);
		inputStream.close();

		outputter = new XMLOutputter();
		outputter.setFormat(org.jdom.output.Format.getPrettyFormat());
		
		/*
		 * outputter.setIndent(" "); outputter.setTextNormalize(true); outputter.setNewlines(true);
		 */
		if (!security_xml.getRootElement().getNamespaceURI().equals(""))
			throw new JDOMException("Bitte keine Namespaces in der security.xml angeben.");
	}

	/**
	 * Sucht nach einem Login-Config-Eintrag mit dem angegebenen Namen.
	 * 
	 * @param name
	 * @return
	 */
	private Element getLoginConfig(String name) {
		Element root = login_config_xml.getRootElement();
		Iterator it = root.getChildren("application-policy").iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.getAttribute("name").getValue().equals(name)) return element;
		}
		return null;
	}

	/**
	 * Entfernt den Login-Config-Eintrag mit dem angegebenen Namen.
	 */
	public void removeApplicationPolicy() {
		Element root = login_config_xml.getRootElement();
		Element toRemove = getLoginConfig(appname);
		if (toRemove != null) root.removeContent(toRemove);
	}

	/**
	 * Entfernt den Security-Constraint-Eintrag mit dem angegebenen Namen.
	 */
	public void removeSecurityConstraints() {
		Element root = web_xml.getRootElement();
		root.removeChildren("security-constraint");
	}
	
	/**
	 * Erzeugt ein jbossweb.xml anhand der übrigen Daten.
	 */
	public void createJBossweb() throws IOException {
		Element root = new Element("jboss-web");
		Element secDomain = new Element("security-domain");
		secDomain.setText("java:/jaas/"+appname);
		root.addContent(secDomain);
		Document doc = new Document(root);
		FileOutputStream outputStream = new FileOutputStream(jbossweb_file);
		outputter.output(doc, outputStream);
		outputStream.close();

	}

	/**
	 * Erzeugt einen neuen Login-Config-Eintrag in der login-config.xml anhand der Daten in der
	 * security.xml.
	 */
	public void createApplicationPolicy() throws MissConfigurationException {
		// Login-Modul-Eintrag konstruieren
		Element policyEl = new Element("application-policy");
		policyEl.setAttribute("name", appname);
		Element authEl = new Element("authentication");

		Element moduleEl = new Element("login-module");
		moduleEl.setAttribute("flag", "required");
		// In Abhängigkeit von dem erforderlichen Login-Modul den Modul-Eintrag erzeugen
		if (getLoginModuleType() == LOGIN_DATABASE) {
			// Optionen aus der security.xml übernehmen
			Map options = getDatabaseSecModuleOptions();
			// Login-Modul von JBoss spezifizieren
			moduleEl.setAttribute("code", "org.jboss.security.auth.spi.DatabaseServerLoginModule");
			// Optionen setzen
			Element jdbcOption;
			
			// JNDI-Pfad zum Datasource
			jdbcOption = new Element("module-option");
			jdbcOption.setAttribute("name", "dsJndiName");
			jdbcOption.setText(NamingConventions.getDatasourceJndiLinkForAppname(appname));
			moduleEl.addContent(jdbcOption);

			// Principals Query
			String principalsQuery = "SELECT "+options.get("authentication.password-column")+" FROM "+options.get("authentication.table")+" WHERE "+options.get("authentication.user-column")+"=?";
			jdbcOption = new Element("module-option");
			jdbcOption.setAttribute("name", "principalsQuery");
			jdbcOption.setText(principalsQuery);
			moduleEl.addContent(jdbcOption);

			// Principals Query
			String rolesQuery = "SELECT "+options.get("authorization.role-column")+", \"Roles\" FROM "+options.get("authorization.table")+" WHERE "+options.get("authorization.user-column")+"=?";
			jdbcOption = new Element("module-option");
			jdbcOption.setAttribute("name", "rolesQuery");
			jdbcOption.setText(rolesQuery);
			moduleEl.addContent(jdbcOption);

		}

		// Baum aufbauen
		authEl.addContent(moduleEl);
		policyEl.addContent(authEl);
		// Testeintrag erzeugen
		login_config_xml.getRootElement().addContent(policyEl);
	}

	/**
	 * Erzeugt einen Security-Constraint-Eintrag in der web.xml anhand der Informationen aus der
	 * security.xml.
	 */
	public void createSecurityConstraints() {
		// Security-Constraint-Einträge aus der security.xml holen
		Element secroot = security_xml.getRootElement();
		// Security-Constraints durchlaufen und in die web.xml einfügen
		Iterator it = secroot.getChildren("security-constraint").iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			// Unterscheide zwischen J2EE-Standard-Constraints und dmerce-spezifischen Constraints
			if (element.getChild("workflow") == null)
				insertSecurityConstraint(element);
			else {
				Element j2eeConformConstraint = convertToJ2eeConstraint(element);
				insertSecurityConstraint(j2eeConformConstraint);
			}
		}
	}
	
	/**
	 * Erzeugt aus einem dmerce-spezifischen Security-Constraint für Workflows einen J2EE-konformen
	 * Security-Constraint.
	 */
	private Element convertToJ2eeConstraint(Element element) {
		Element converted = (Element) element.clone();
		converted.removeChildren("workflow");
		Iterator it = element.getChildren("workflow").iterator();
		while (it.hasNext()) {
			Element workflowEl = (Element) it.next();
			Element webResCol = new Element("web-resource-collection");
			Element webResName = new Element("web-resource-name");
			webResName.setText("dmerce internal secured area for workflow "+workflowEl.getAttributeValue("id"));
			Element urlPattern = new Element("url-pattern");
			urlPattern.setText(NamingConventions.getSecuredAreaPathForWorkflow(workflowEl.getAttributeValue("id"))+"/*");
			webResCol.addContent(webResName);
			webResCol.addContent(urlPattern);
			converted.getChildren().add(0, webResCol);
		}
		return converted;
	}

	/**
	 * Fügt ein Security-Constraint-Element in die web.xml ein
	 * 
	 * @param securityConstraint einzufügendes Element
	 */
	private void insertSecurityConstraint(Element securityConstraint) {
		assert securityConstraint.getName().equals("security-constraint");
		// Insertion-Point in web.xml suchen
		Element webroot = web_xml.getRootElement();
		int insertionPoint = getSecurityConstraintInsertionPoint();
		Element clonedElement = (Element) securityConstraint.clone();
		// clonedElement.setNamespace(null);
		webroot.getChildren().add(insertionPoint, clonedElement);
	}

	/**
	 * Sucht gemäß der DTD für Servlets 2.3 nach der korrekten Position zum Einfügen des
	 * Security-Constraint-Elementes.
	 * 
	 * @return Gibt die Position (begonnen bei 0) zurück, an der das Element eingefügt werden
	 *         sollte.
	 */
	private int getSecurityConstraintInsertionPoint() {
		Element root = web_xml.getRootElement();
		int counter = 0;
		Iterator it = root.getChildren().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			String elName = element.getName();
			if (elName.equals("login-config") | elName.equals("security-role")
					| elName.equals("env-entry") | elName.equals("ejb-ref") | elName.equals("ejb-local-ref")) return counter;
			counter++;
		}
		return counter;
	}
	
	/**
	 * Erzeugt einen Login-Config-Eintrag in der web.xml aus den Informationen in der security.xml.
	 */
	public void createLoginConfig() {
		// Login-Config-Eintrag aus der security.xml holen
		Element secroot = security_xml.getRootElement();
		Element login = secroot.getChild("login");
		Element loginPage = login.getChild("login-page");
		Element errorPage = login.getChild("error-page");
		// Elemente der web.xml erzeugen
		Element loginConfig = new Element("login-config");
		Element authMethod = new Element("auth-method");
		authMethod.setText("FORM");
		loginConfig.addContent(authMethod);
		Element formLoginConfig = new Element("form-login-config");
		Element formLoginPage = new Element("form-login-page");
		formLoginPage.setText(loginPage.getText());
		formLoginConfig.addContent(formLoginPage);
		Element formErrorPage = new Element("form-error-page");
		formErrorPage.setText(errorPage.getText());
		formLoginConfig.addContent(formErrorPage);
		loginConfig.addContent(formLoginConfig);
		// Login-Config in die web.xml einfügen
		Element webroot = web_xml.getRootElement();
		webroot.getChildren().add(getLoginConfigInsertionPoint(), loginConfig);
	}

	/**
	 * Sucht gemäß der DTD für Servlets 2.3 nach der korrekten Position zum Einfügen des
	 * Login-Config-Elementes.
	 * 
	 * @return Gibt die Position (begonnen bei 0) zurück, an der das Element eingefügt werden
	 *         sollte.
	 */
	private int getLoginConfigInsertionPoint() {
		Element root = web_xml.getRootElement();
		int counter = 0;
		Iterator it = root.getChildren().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			String elName = element.getName();
			if (elName.equals("security-role")
					| elName.equals("env-entry") | elName.equals("ejb-ref") | elName.equals("ejb-local-ref")) return counter;
			counter++;
		}
		return counter;
	}
	
	/**
	 * Entfernt frühere Login-Config-Einträge in der web.xml.
	 */
	public void removeLoginConfig() {
		Element root = web_xml.getRootElement();
		root.removeChildren("login-config");
	}

	/**
	 * Ermittelt, welcher Login-Typ in der security.xml eingestellt worden ist.
	 * 
	 * @return Login-Typ als int-Konstante (Die Konstanten sind in dieser Klasse definiert.)
	 * @throws MissConfigurationException wird geworfen, falls kein Login-Typ korrekt eingestellt
	 *         worden ist.
	 */
	private int getLoginModuleType() throws MissConfigurationException {
		Element root = security_xml.getRootElement();
		Iterator it = root.getChildren().iterator();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			if (el.getName().equals("database-sec")) {
				return LOGIN_DATABASE;
			}
		}
		throw new MissConfigurationException("No LoginModule configured.");
	}

	private Map getDatabaseSecModuleOptions() {
		HashMap map = new LinkedHashMap();
		Element root = security_xml.getRootElement();
		Element sec = getChild(root, "database-sec");
		Element authentication = getChild(sec, "authentication");
		map.put("authentication.table", getChild(authentication, "table").getText());
		map.put("authentication.user-column", getChild(authentication, "user-column").getText());
		map.put("authentication.password-column", getChild(authentication, "password-column")
				.getText());
		Element authorization = getChild(sec, "authorization");
		map.put("authorization.table", getChild(authorization, "table").getText());
		map.put("authorization.user-column", getChild(authorization, "user-column").getText());
		map.put("authorization.role-column", getChild(authorization, "role-column").getText());
		return map;
	}

	/**
	 * Hilfsmethode, die ein Kindelement mit einem bestimmten Namen sucht. Hinweis: Dies ist
	 * lediglich eine Hilfsmethode, da die getChild-Methode von Element nicht korrekt zu
	 * funktionieren scheint.
	 * 
	 * @param parent
	 * @param childName
	 * @return
	 */
	private Element getChild(Element parent, String childName) {
		Iterator it = parent.getChildren().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.getName().equals(childName)) return element;
		}
		return null;
	}

	/**
	 * Schreibt die login-config.xml mit dem aktuellen Zustand in die Datei zurück.
	 * 
	 * @throws IOException
	 */
	public void writeLoginXml() throws IOException {
		FileOutputStream outputStream = new FileOutputStream(login_file);
		outputter.output(login_config_xml, outputStream);
		outputStream.close();
	}

	/**
	 * Schreibt die web.xml mit dem aktuellen Zustand in die Datei zurück.
	 * 
	 * @throws IOException
	 */
	public void writeWebXml() throws IOException {
		FileOutputStream outputStream = new FileOutputStream(web_file);
		outputter.output(web_xml, outputStream);
		outputStream.close();
	}

	/**
	 * Test
	 */
	public static void main(String[] args) {
		File security = new File("C:\\Temp\\test\\security.xml");
		File login = new File("C:\\Temp\\test\\login-config.xml");
		File web = new File("C:\\Temp\\test\\web.xml");
		File jbossweb = new File("C:\\Temp\\test\\jboss-web.xml");
		try {
			/*
			 * Der nachfolgende Code stellt den empfohlenen Ablauf für die Benutzung dieser Klasse
			 * dar.
			 */
			System.out.println("1.");
			SecurityConfigWriter instance = new SecurityConfigWriter("dummy", security, login, web, jbossweb);
			System.out.println("2.");
			instance.removeApplicationPolicy();
			System.out.println("3.");
			instance.createApplicationPolicy();
			System.out.println("4.");
			instance.removeLoginConfig();
			System.out.println("5.");
			instance.createLoginConfig();
			System.out.println("6.");
			instance.removeSecurityConstraints();
			System.out.println("7.");
			instance.createSecurityConstraints();
			System.out.println("8.");
			instance.createJBossweb();
			instance.writeWebXml();
			System.out.println("9.");
			instance.writeLoginXml();
			System.out.println("OK.");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MissConfigurationException e) {
			e.printStackTrace();
		}
	}

}
