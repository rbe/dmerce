/*
 * Datei angelegt am 02.03.2004
 */
package com.wanci.dmerce.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * DatasourceTask ist eine Wrapper-Klasse, die den DatasourceWriter in Ant-Taks nutzbar macht.
 * 
 * @author Masanori Fujita
 */
public class DatasourceTask extends Task {

	private File properties_file;

	private File datasource_file;

	private String appname;

	public DatasourceTask() {
	}

	public void setProperties(File file) {
		this.properties_file = file;
	}

	public void setDatasource(File file) {
		this.datasource_file = file;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public void execute() throws BuildException {
		try {
			DatasourceWriter writer = new DatasourceWriter(appname, properties_file,
					datasource_file);
			writer.createDatasourceFile();
			System.out.println(datasource_file.getAbsolutePath() + " created successfully.");
		} catch (Exception e) {
			System.out.println("Error occured while creating " + datasource_file.getAbsolutePath()
					+ ".");
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

}
