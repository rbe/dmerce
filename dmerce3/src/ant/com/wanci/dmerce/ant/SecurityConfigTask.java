/*
 * Datei angelegt am 02.03.2004
 */
package com.wanci.dmerce.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * SecurityConfigTask ist eine Wrapper-Klasse, die den SecurityConfigWriter in Ant-Taks nutzbar
 * macht.
 * 
 * @author Masanori Fujita
 */
public class SecurityConfigTask extends Task {

	private File security_file;

	private File web_file;

	private File loginconfig_file;

	private String appname;

	private File jbossweb_file;

	public SecurityConfigTask() {
	}

	public void setSecurity(File file) {
		this.security_file = file;
	}

	public void setWeb(File file) {
		this.web_file = file;
	}

	public void setLoginconfig(File file) {
		this.loginconfig_file = file;
	}
	
	public void setJbossweb(File file) {
		this.jbossweb_file = file;
	}
	
	public void setAppname(String appname) {
		this.appname = appname;
	}

	public void execute() throws BuildException {
		try {
			SecurityConfigWriter instance = new SecurityConfigWriter(appname, security_file,
					loginconfig_file, web_file, jbossweb_file);
			System.out.println("Modifying "+loginconfig_file.getAbsolutePath()+"...");
			instance.removeApplicationPolicy();
			instance.createApplicationPolicy();
			System.out.println("Modifying "+web_file.getAbsolutePath()+"...");
			instance.removeLoginConfig();
			instance.createLoginConfig();
			instance.removeSecurityConstraints();
			instance.createSecurityConstraints();
			instance.writeWebXml();
			System.out.println(loginconfig_file.getAbsolutePath()+" created successfully.");
			instance.writeLoginXml();
			System.out.println(web_file.getAbsolutePath()+" created successfully.");
			instance.createJBossweb();
			System.out.println(jbossweb_file.getAbsolutePath()+" created successfully.");
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}
