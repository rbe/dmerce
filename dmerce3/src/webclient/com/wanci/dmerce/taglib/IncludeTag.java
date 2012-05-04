/*
 * IncludeTag.java
 *
 * Created on September 11, 2003, 4:50 PM
 */

package com.wanci.dmerce.taglib;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Bindet eine beliebige externe html/text-Ressource in die Seite ein.
 * @author  mf
 */
public class IncludeTag extends TagSupport {
    
    String urlstring;
    
    /** Creates a new instance of IncludeTag */
    public IncludeTag() {
        
    }
    
    public int doStartTag() throws JspTagException {
        try {
            URL url = new URL(urlstring);
            URLConnection urlcon = url.openConnection();
            System.out.println("HttpURLConnection erzeugen.");
            HttpURLConnection con = (HttpURLConnection) urlcon;
            System.out.println("GET setzen");
            con.setRequestMethod("GET");
            System.out.println("Connect...");
            con.connect();
            System.out.print("Content Type: ");
            System.out.println(con.getContentType());
            // Inhalte auslesen
            InputStream is = con.getInputStream();
            InputStream buffer = new BufferedInputStream(is);
            Reader r = new InputStreamReader(buffer);
            StringWriter stringwriter = new StringWriter();
            // System.out.println("Content: ");
            int c;
            while ((c = r.read()) != -1) {
                stringwriter.write(c);
            }
            String htmltext = stringwriter.toString();
            
            StringTokenizer st = new StringTokenizer(htmltext, "\n");
            StringBuffer sb = new StringBuffer();
            boolean skip = true;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.indexOf("<html") >= 0)
                    skip = true;
                else if (s.indexOf("<body") >= 0) {
                    skip = false;
                    continue;
                }
                else if (s.indexOf("</body") >= 0)
                    skip = true;
                if (!skip)
                    sb.append(s + "\n");
            }
            
            StringReader stringReader = new StringReader(sb.toString());
            JspWriter jspWriter = pageContext.getOut();
            while ((c = stringReader.read()) != -1) {
                jspWriter.write(c);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }
    
    public int doEndTag() {
        return EVAL_PAGE;
    }
    
    public void setUrl(String value) {
        this.urlstring = value;
    }
    
    public String getUrl() {
        return urlstring;
    }
    
}
