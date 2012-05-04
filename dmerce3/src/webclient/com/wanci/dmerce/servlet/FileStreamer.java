/*
 * Datei angelegt am 02.02.2004
 */
package com.wanci.dmerce.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * Der FileStreamer dienst als Wrapper-Servlet, um auf Dateien zuzugreifen, die
 * zuvor per Form-Upload hochgeladen wurden. Es wird erwartet, dass der Name der
 * Ressource auf dem Server bekannt ist. <p/>
 * 
 * Aufgerufen wird dieses Servlet (in Abh. vom Servlet-Mapping in web.xml) z.B.
 * mit "http://localhost/files/foo.gif?file=12345_foo.gif"
 * 
 * @author Masanori Fujita
 */
public class FileStreamer extends HttpServlet {

	private XmlPropertiesReader properties;

	private String STORE_PATH;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String fileName = STORE_PATH + "/" + request.getParameter("file");

		// Ziel-Ressource als Stream rausschreiben
		File target = new File(fileName);
		response.setContentLength((int) target.length());
		FileInputStream inputStream = new FileInputStream(target);

		int data;
		while ((data = inputStream.read()) != -1) {
			response.getOutputStream().write(data);
		}

		inputStream.close();

	}

	public void init(ServletConfig arg0) throws ServletException {

		super.init(arg0);

		try {

			properties = XmlPropertiesReader.getInstance();
			STORE_PATH = properties.getProperty("fileupload.path");

		} catch (XmlPropertiesFormatException e) {
			throw new ServletException(e);
		}

	}
}