/*
 * Created on 12.09.2004
 *
 */
package com.wanci.dmerce.taglib.auth;

import java.util.StringTokenizer;

import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.servlet.AuthInfo;
import com.wanci.java.LangUtil;

/**l
 * @author rb
 * @version $Id$
 * 
 * Zeigt seinen Body dann an, wenn ein Benutzer eingeloggt ist und wahlweise
 * einem Realm angehört und/oder der angegebene Benutzername ist. Wird nichts
 * angegeben, so wird nur zwischen eingeloggt/nicht eingeloggt unterschieden.
 *  
 */
public class LoggedInTag extends BodyTagSupport {

	private String username = null;

	private String realm = null;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public int doStartTag() {

		int r = SKIP_BODY;

		// Hole AuthInfo aus Session Context
		AuthInfo authInfo = (AuthInfo) pageContext.getSession().getAttribute(
				"qAuthInfo");
		
		// Wenn AuthInfo Objekt != null dann Benutzernamen und Realm auswerten
		// ansonsten Body übergehen
		if (authInfo != null) {

			// Wurde kein Benutzername oder Realm angegeben, dann soll nur
			// danach unterschieden werden, "ob eingeloggt oder nicht"
			// (nur authInfo != null)
			if (username == null && realm == null)
				r = EVAL_BODY_INCLUDE;

			// Entspricht Benutzername oder Realm dem geforderten (es koennen
			// kommagetrennt mehrere Werte uebergeben/geprueft werden, dann Body
			// auswerten, ansonsten überspringen
			if (username != null | realm != null) {
				
				// Kommata entfernen, da wir diese als Trennzeichen nutzen
				// SQL BUG: entfernen von ' und " aus dem Benutzernamen/Realm
				// Sicherheit: entfernen einer Pipe
				if (username != null) {
					username = username.replace('\'', ' ');
					username = username.replace('"', ' ');
					username = username.replace('|', ' ');
				}
				if (realm != null) {
					realm = realm.replace('\'', ' ');
					realm = realm.replace('"', ' ');
					realm = realm.replace('|', ' ');
				}

				StringTokenizer st;

				// Pruefe, ob eingeloggter Benutzername in der Liste der im
				// Attribut user= angegebenen enthalten ist
				if (username != null) {

					st = new StringTokenizer(username, ",");
					while (st.hasMoreTokens()) {

						String u = st.nextToken();
						
						LangUtil.consoleDebug(true, "Checking for username: "
								+ u + " == " + authInfo.getUsername() + "?");

						if (authInfo.getUsername().indexOf(u) >= 0)
							r = EVAL_BODY_INCLUDE;

					}

				}

				// Pruefe, ob eingeloggter Benutzer einem Realm angehoert, der
				// in der Liste der im Attribut realm= angegebenen enthalten ist
				if (realm != null) {

					st = new StringTokenizer(realm, ",");
					while (st.hasMoreTokens()) {

						String re = st.nextToken();

						LangUtil.consoleDebug(true, "Checking for realm: "
								+ re + " == " + authInfo.getRealm() + "?");

						if (authInfo.getRealm().indexOf(re) >= 0)
							r = EVAL_BODY_INCLUDE;

					}

				}

			}

		} else {

			r = SKIP_BODY;

		}

		return r;

	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

}