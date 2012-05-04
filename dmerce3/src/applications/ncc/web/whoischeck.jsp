<%@ page import="com.wanci.dmerce.ncc.whois.*" %>

<%

	String logPrefix = "[whoischeck] ";
	String domain = request.getParameter("d");
	String tld = request.getParameter("tld");
	if (tld != null)
		domain += "." + tld;
	String availablePage = request.getParameter("ap");
	String unavailablePage = request.getParameter("uap");
	String errorPage = request.getParameter("ep");
	String destination;
	
	System.out.println(logPrefix + "domain=" + domain + ", " + domain.length());
	System.out.println(logPrefix + "availablePage=" + availablePage);
	System.out.println(logPrefix + "unavailablePage=" + unavailablePage);
	System.out.println(logPrefix + "errorPage=" + errorPage);
	
	if (domain.length() > 3 && availablePage != null
		&& unavailablePage != null && errorPage != null) {
		
		System.out.println(logPrefix + "Querying domain " + domain);

		try {
		
			WhoisQuery whoisQuery = new WhoisQuery(domain);
			whoisQuery.query();
			WhoisAnswerHandler hdl = whoisQuery.getHandler();
			if (hdl != null) {
				String ref = hdl.getReferralWhoisServer();
				if (ref != null) {
					whoisQuery.setWhoisServer(ref);
					whoisQuery.query();
				}
				if (hdl.isDomainAvailable()) {
					System.out.println(logPrefix + "Domain " + domain
						+ " available");
					destination = availablePage;
				}
				else {
					System.out.println(logPrefix + "Domain " + domain
						+ " unavailable");
					destination = unavailablePage;
				}
			}
			else
				destination = errorPage;
				
		}
		catch (Exception e) {
			System.out.println(logPrefix + "EXCEPTION: " + e.getCause()
				+ ": " + e.getMessage());
			e.printStackTrace();
			destination = errorPage;
		}
		
	}
	else if (errorPage == null) {
		out.println(logPrefix + "Shit happens.");
		destination = null;
	}
	else
		destination = errorPage;
	
	if (destination != null) {
		
%>

<jsp:forward page="<%= destination %>">
    <jsp:param name="d" value="<%= domain %>"/>
</jsp:forward>

<%

	}

%>
