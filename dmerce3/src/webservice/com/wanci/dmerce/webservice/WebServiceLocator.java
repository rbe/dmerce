/*
 * Created on Aug 21, 2003
 */
package com.wanci.dmerce.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.webservice.db.SQLWebservice;
import com.wanci.java.LangUtil;

/**
 * @author mf
 * @version $Id: WebServiceLocator.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 */
public class WebServiceLocator {

	private boolean DEBUG = false;
	private boolean DEBUG2 = false;

	private static WebServiceLocator singletonInstance;

	private ServiceFactory serviceFactory;

	private String ENDPOINT_ADDRESS = "";
	private URL wsdlUrl;

	private SQLWebservice stubSQLWebservice;

	private WebServiceLocator() throws DmerceException {

		try {
			DEBUG =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean("debug");
			DEBUG2 =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");
		}
		catch (XmlPropertiesFormatException e) {
		}

		LangUtil.consoleDebug(DEBUG2, this, "Initializing");

		stubSQLWebservice = null;

		//setze Endpoint Addresse
		ENDPOINT_ADDRESS =
			XmlPropertiesReader.getInstance().getProperty("endpointaddress");
		
		LangUtil.consoleDebug(
			DEBUG2,
			this,
			"Constructing service locator. Dynamic connection to '"
				+ ENDPOINT_ADDRESS
				+ "'");

		try {

			serviceFactory = ServiceFactory.newInstance();
			generateWsdlUrl();
			stubSQLWebservice =
				(SQLWebservice) locateWebservice("urn:dmerce",
					"SQLWebservice",
					"SQLWebservicePort");

		}
		catch (ServiceException e) {

			stubSQLWebservice = null;
			throw new DmerceException(
				"Cannot connect to webservice at address '"
					+ ENDPOINT_ADDRESS
					+ "'");

		}
		catch (MalformedURLException e) {

			throw new DmerceException(
				"Cannot connect to webservice due to malformed url: '"
					+ ENDPOINT_ADDRESS
					+ "'");

		}

		LangUtil.consoleDebug(
			DEBUG2,
			this,
			"Webservice stub to '"
				+ ENDPOINT_ADDRESS
				+ "' successfully constructed");
	}

	protected void generateWsdlUrl() throws MalformedURLException {

		String urlString = ENDPOINT_ADDRESS + "?WSDL";
		wsdlUrl = new URL(urlString);

		LangUtil.consoleDebug(
			DEBUG2,
			this,
			"Generated WSDL URL: '" + urlString + "'");

	}

	/**
	 * Versucht einen Proxy zu dem spezifizierten Webservice zu erzeugen.
	 */
	protected Remote locateWebservice(
		String nameSpaceUri,
		String serviceName,
		String portName)
		throws ServiceException {

		Service aService =
			serviceFactory.createService(
				wsdlUrl,
				new QName(nameSpaceUri, serviceName));

		Remote proxy =
			aService.getPort(
				new QName(nameSpaceUri, portName),
				SQLWebservice.class);

		return proxy;
	}

	/**
	 * @see com.wanci.dmerce.webservice.WebServiceLocator#getSQLQueryService()
	 */
	public SQLWebservice getSQLWebservice() throws DmerceException {

		if (stubSQLWebservice != null)
			return stubSQLWebservice;
		else {

			LangUtil.consoleDebug(DEBUG2, this, "");

			throw new DmerceException(
				"Webservice was not initialized" + " correctly. Sorry.");

		}

	}

	public static WebServiceLocator getInstance() throws DmerceException {

		if (singletonInstance == null)
			singletonInstance = new WebServiceLocator();

		return singletonInstance;

	}

}
