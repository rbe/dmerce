/*
 * Created on Apr 23, 2003
 */
/**
 * @author mm
 * @version $Id: GetFilesInfo.java,v 1.1 2004/03/29 13:39:32 rb Exp $
 */
public class GetFilesInfo {
	
	String url;
	String filename;
	String host;
	int port;
	String protocol;
	String path;
	String userInfo;
	String username;
	String password;
	
	/**
	 * getter for filename
	 * @return name of file
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * getter for host
	 * @return host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * getter for password
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * getter for path
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * getter for port
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * getter for protocol
	 * @return protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * getter for URL
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * getter for UserInfo
	 * @return userinfo
	 */
	public String getUserInfo() {
		return userInfo;
	}

	/**
	 * getter for username
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param string
	 */
	public void setFilename(String string) {
		filename = string;
	}

	/**
	 * @param string
	 */
	public void setHost(String string) {
		host = string;
	}

	/**
	 * @param string
	 */
	public void setPassword(String string) {
		password = string;
	}

	/**
	 * @param string
	 */
	public void setPath(String string) {
		path = string;
	}

	/**
	 * @param i
	 */
	public void setPort(int i) {
		port = i;
	}

	/**
	 * @param string
	 */
	public void setProtocol(String string) {
		protocol = string;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string) {
		url = string;
	}

	/**
	 * @param string
	 */
	public void setUserInfo(String string) {
		userInfo = string;
	}

	/**
	 * @param string
	 */
	public void setUsername(String string) {
		username = string;
	}

}
