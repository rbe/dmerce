/*
 * Created on 26.04.2003
 *  
 */
package com.wanci.ncc.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author rb
 * @version $Id: Nameserver.java,v 1.2 2004/02/28 22:39:42 rb Exp $
 * 
 * ListenOn Forwarders Primary zones Secondary zones
 *  
 */
public class Nameserver {

	/**
	 *  
	 */
	private String name;
	/**
	 *  
	 */
	private Vector forwarders = new Vector();
	/**
	 *  
	 */
	private Vector listenOn = new Vector();
	/**
	 *  
	 */
	private Vector primaryZones = new Vector();
	/**
	 *  
	 */
	private boolean hasPrimaryZones = false;
	/**
	 *  
	 */
	private Vector secondaryZones = new Vector();
	/**
	 *  
	 */
	private boolean hasSecondaryZones = false;
	/**
	 * 
	 *  
	 */
	public Nameserver(String name) {
		this.name = name;
	}

	/**
	 * @param forwarder
	 */
	public void addForwarder(InetAddress forwarder) {
		forwarders.add(forwarder);
	}

	/**
	 * @param listenOn
	 */
	public void addListenOn(InetAddress listenOnAddress) {
		listenOn.add(listenOnAddress);
	}

	/**
	 * @param z
	 */
	public void addPrimaryZone(Zone z) {
		primaryZones.add(z);
		hasPrimaryZones = true;
	}

	/**
	 * @param z
	 */
	public void addSecondaryZone(Zone z) {
		secondaryZones.add(z);
		hasSecondaryZones = true;
	}

	/**
	 * Generate configuration statements for primary and secondary zones
	 * 
	 * zone "1ci.de" { type master; file
	 * "/opt/dmerce/var/dns/irb.ns1.1xsp.com/pri/1ci.de"; };
	 * 
	 * zone "vsv-provinzial.de" { type slave; file "s/vsv-provinzial.de";
	 * masters { 213.128.138.193; }; };
	 */
	public String generateZoneConfiguration() throws UnknownHostException {

		Iterator i;
		StringBuffer sb = new StringBuffer();

		i = primaryZones.iterator();
		while (i.hasNext()) {

			Zone z = (Zone) i.next();

			sb.append(
				"\nzone \""
					+ z.getName()
					+ "\" {\n"
					+ "\ttype master;\n"
					+ "\tfile \"/opt/dmerce/var/dns/"
					+ name
					+ "/pri/"
					+ z.getName()
					+ "\";\n"
					+ "};\n");

		}

		i = secondaryZones.iterator();
		while (i.hasNext()) {

			Zone z = (Zone) i.next();

			sb.append(
				"\nzone \""
					+ z.getName()
					+ "\" {\n"
					+ "\ttype slave;\n"
					+ "\tfile \"/opt/dmerce/var/dns/"
					+ name
					+ "/sec/"
					+ z.getName()
					+ "\";\n"
					+ "\tmasters {\n"
					+ "\t\t"
					+ InetAddress
						.getByName(z.getNameserverRecord(0).getValue())
						.getHostAddress()
					+ ";\n"
					+ "\t};\n"
					+ "};\n");

		}

		return sb.toString();

	}

	/**
	 * Generates named.conf file for nameserver
	 * 
	 * options { directory "/opt/dmerce/var/dns/irb.ns1.1xsp.com"; pid-file
	 * "named.pid"; listen-on { 213.128.138.193; }; };
	 * 
	 * controls { unix "/opt/dmerce/var/run/ndc.irb.ns1.1xsp.com" perm 0600
	 * owner 0 group 0; };
	 * 
	 * logging { channel my_syslog { syslog daemon; severity info; }; channel
	 * my_file { file "irb.ns1.1xsp.com.log"; severity dynamic; }; category
	 * default { my_syslog; }; category statistics { my_syslog; my_file; };
	 * category queries { my_file; }; category default { my_syslog; }; category
	 * panic { my_syslog; }; category packet { my_file; }; category eventlib {
	 * my_file; }; category lame-servers { null; }; };
	 * 
	 * zone "." { type hint; file "/opt/dmerce/var/dns/named.root"; };
	 * 
	 * zone "localhost" { type master; file "/opt/dmerce/var/dnslocalhost"; };
	 * 
	 * zone "127.in-addr.arpa" { type master; file
	 * "/opt/dmerce/var/dns/127.in-addr.arpa"; };
	 * 
	 * include "/opt/dmerce/var/dns/irb.ns1.1xsp.com/pri.zones"; include
	 * "/opt/dmerce/var/dns/irb.ns1.1xsp.com/sec.zones";
	 * 
	 * @return
	 */
	public String getBindString() throws UnknownHostException {

		Iterator i;

		StringBuffer sb =
			new StringBuffer(
				"options {\n"
					+ "\tdirectory \"/opt/dmerce/var/dns/"
					+ name
					+ "\";\n"
					+ "\tpid-file \"named.pid\";\n"
					+ "\tlisten-on {\n");

		i = listenOn.iterator();
		while (i.hasNext()) {

			sb.append(
				"\t\t" + ((InetAddress) i.next()).getHostAddress() + ";\n");

		}

		sb.append(
			"\t};\n"
				+ "};\n"
				+ "\n"
				+ "controls {\n"
				+ "\tunix \"/opt/dmerce/var/run/ndc."
				+ name
				+ "\"\n"
				+ "\tperm 0600\n"
				+ "\towner 0\n"
				+ "\tgroup 0;\n"
				+ "};\n"
				+ "\n"
				+ "logging {\n"
				+ "\tchannel my_syslog {\n"
				+ "\t\tsyslog daemon;\n"
				+ "\t\tseverity info;\n"
				+ "\t};\n"
				+ "\tchannel my_file {\n"
				+ "\t\tfile \""
				+ name
				+ ".log\";\n"
				+ "\t\tseverity dynamic;\n"
				+ "\t};\n"
				+ "\tcategory default { my_syslog; };\n"
				+ "\tcategory statistics { my_syslog; my_file; };\n"
				+ "\tcategory queries { my_file; };\n"
				+ "\tcategory default { my_syslog; };\n"
				+ "\tcategory panic { my_syslog; };\n"
				+ "\tcategory packet { my_file; };\n"
				+ "\tcategory eventlib { my_file; };\n"
				+ "\tcategory lame-servers { null; };\n"
				+ "};\n"
				+ "\n"
				+ "zone \".\" {\n"
				+ "\ttype hint;\n"
				+ "\tfile \"/opt/dmerce/var/dns/named.root\";\n"
				+ "};\n"
				+ "\n"
				+ "zone \"localhost\" {\n"
				+ "\ttype master;\n"
				+ "\tfile \"/opt/dmerce/var/dns/localhost\";\n"
				+ "};\n"
				+ "\n"
				+ "zone \"127.in-addr.arpa\" {\n"
				+ "\ttype master;\n"
				+ "\tfile \"/opt/dmerce/var/dns/127.in-addr.arpa\";\n"
				+ "};\n"
				+ generateZoneConfiguration());

		return sb.toString();

	}

	public String getName() {
		return name;
	}

	/**
	 * Return iterator over primary zones of this nameserver
	 * 
	 * @return
	 */
	public Iterator getPrimaryZonesIterator() {
		return primaryZones.iterator();
	}

	/**
	 * Return iterator over secondary zones of this nameserver
	 * 
	 * @return
	 */
	public Iterator getSecondaryZonesIterator() {
		return secondaryZones.iterator();
	}

	/**
	 * Does this nameserver have primary zones?
	 * @return
	 */
	public boolean hasPrimaryZones() {
		return hasPrimaryZones;
	}
	
	/**
	 * Does this nameserver have secondary zones?
	 * @return
	 */
	public boolean hasSecondaryZones() {
		return hasSecondaryZones;
	}

	/**
	 * @param forwarders
	 */
	public void setForwarders(Vector forwarders) {
		this.forwarders = forwarders;
	}

	/**
	 * @param listenOn
	 */
	public void setListenOn(Vector listenOn) {
		this.listenOn = listenOn;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

}