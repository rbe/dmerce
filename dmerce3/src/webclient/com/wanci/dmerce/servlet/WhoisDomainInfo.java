/*
 * Created on 14.03.2004
 *  
 */
package com.wanci.dmerce.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanci.ncc.whois.WhoisAnswerHandler;
import com.wanci.ncc.whois.WhoisQuery;

/**
 * @author rb
 * @version $$Id: WhoisDomainInfo.java,v 1.1 2004/03/15 11:35:18 rb Exp $$
 *  
 */
public class WhoisDomainInfo extends HttpServlet {

    private boolean DEBUG = false;

    private String logPrefix = "[WhoisServlet] ";

    private String domain;

    private String tld;

    private String availablePage;

    private String unavailablePage;

    private String errorPage;

    private String destination;

    private HttpServletRequest request;

    private HttpServletResponse response;

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    private void getParameters() {

        String debug = request.getParameter("debug");
        if (debug.equals("1") || debug.equals("yes"))
            DEBUG = true;

        domain = request.getParameter("d");
        tld = request.getParameter("tld");
        if (tld != null)
            domain += "." + tld;

        availablePage = request.getParameter("ap");
        unavailablePage = request.getParameter("uap");
        errorPage = request.getParameter("ep");

    }

    private void queryWhois() {

        System.out.println(logPrefix + "Querying domain '" + domain + "'");

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

            System.out.println(logPrefix + "EXCEPTION: " + e.getCause() + ": "
                    + e.getMessage());

            if (DEBUG)
                e.printStackTrace();

            destination = errorPage;

        }

    }

    private void redirect() {

        try {
            response.sendRedirect(destination);
        }
        catch (IOException e) {
            if (DEBUG)
                e.printStackTrace();
        }

    }

    void processRequest(HttpServletRequest request, HttpServletResponse response) {

        this.request = request;

        if (DEBUG) {
            System.out.println(logPrefix + "availablePage=" + availablePage);
            System.out
                    .println(logPrefix + "unavailablePage=" + unavailablePage);
            System.out.println(logPrefix + "errorPage=" + errorPage);
        }

        if (domain.length() > 3 && availablePage != null
                && unavailablePage != null && errorPage != null)
            queryWhois();
        else if (errorPage == null) {

            try {
                response.getWriter().println(
                    logPrefix + "Shit happens: cannot display error page"
                            + "because no URL was given");
            }
            catch (IOException e) {
                if (DEBUG)
                    e.printStackTrace();
            }

            destination = null;

        }
        else
            destination = errorPage;

        if (destination != null)
            redirect();

    }

}
