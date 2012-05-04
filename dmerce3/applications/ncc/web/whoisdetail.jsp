<%@ page import="java.util.Iterator,
	com.wanci.dmerce.ncc.whois.*" %>

<%

	String domain = request.getParameter("d");
	String tld = request.getParameter("tld");
	if (tld != null)
		domain += "." + tld;

	Iterator i;

%>

<html>
<body>

Whois query for: <%= domain %><br>

<%

	WhoisQuery whoisQuery = new WhoisQuery(domain);
    whoisQuery.query();
    i = whoisQuery.getAnswer().iterator();
    while (i.hasNext()) {
		String s = (String) i.next();
        out.println(s + "<br>");
    }

    WhoisAnswerHandler hdl = whoisQuery.getHandler();
    if (hdl != null) {
    	String ref = hdl.getReferralWhoisServer();
        if (ref != null) {
        	whoisQuery.setWhoisServer(ref);
            whoisQuery.query();
			i = whoisQuery.getAnswer().iterator();
            while (i.hasNext()) {
				String s = (String) i.next();
                out.println(s + "<br>");
            }
        }
        out.print("Domain is ");
        if (hdl.isDomainAvailable())
        	out.print("<b>available</b>");
        else
        	out.print("<b>unavailable</b>");
        out.println("<br>");
	}
	
%>

</body>
</html>