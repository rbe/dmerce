/*
 * Created on 19.01.2003
 *
 */
package com.wanci.ncc.servicemonitor;

/**
 * @author rb
 * @version $Id: InetServiceDefinition.java,v 1.1 2004/02/02 09:41:46 rb Exp $
 *
 */
public class InetServiceDefinition implements InetService {

	public String inetServiceName = "No name";

	public String description = "No description available";

	public int[] tcpPorts;

	public int[] udpPorts;

	public InetServiceDefinition() {
	}

	public InetServiceDefinition(
		String inetServiceName,
		String description,
		int[] tcpPorts,
		int[] udpPorts) {

		setInetServiceName(inetServiceName);
		setDescription(description);
		setTcpPorts(tcpPorts);
		setUdpPorts(udpPorts);

	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the inetServiceName.
	 * @return String
	 */
	public String getInetServiceName() {
		return inetServiceName;
	}

	/**
	 * Returns the tcpPorts.
	 * @return int[]
	 */
	public int[] getTcpPorts() {
		return tcpPorts;
	}

	/**
	 * Returns the udpPorts.
	 * @return int[]
	 */
	public int[] getUdpPorts() {
		return udpPorts;
	}

	/**
	 * @see com.wanci.ncc.InetService#setDescription(String)
	 */
	public void setDescription(String description) {
		if (description != null)
			this.description = description;
	}

	/**
	 * @see com.wanci.ncc.InetService#setInetServiceName(String)
	 */
	public void setInetServiceName(String inetServiceName) {
		if (inetServiceName != null)
			this.inetServiceName = inetServiceName;
	}

	/**
	 * @see com.wanci.ncc.InetService#setTcpPorts(int[])
	 */
	public void setTcpPorts(int[] tcpPorts) {
		if (tcpPorts != null)
			this.tcpPorts = tcpPorts;
	}

	/**
	 * @see com.wanci.ncc.InetService#setUdpPorts(int[])
	 */
	public void setUdpPorts(int[] udpPorts) {
		if (udpPorts != null)
			this.udpPorts = udpPorts;
	}

}