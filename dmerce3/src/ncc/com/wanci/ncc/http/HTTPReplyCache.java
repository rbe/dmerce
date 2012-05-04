/**
 * Created on Feb 11, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.http;

import java.util.HashMap;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class HTTPReplyCache {

	private HashMap httpReplies;

	public HTTPReplyCache() {
		httpReplies = new HashMap();
	}

	public void add(HTTPReply httpReply) {
		httpReplies.put(httpReply.getUrl(), httpReply);
	}

	public HTTPReply get(String url) {
		return (HTTPReply) httpReplies.get(url);
	}

}