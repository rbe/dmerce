/*
 * Created on 13.06.2003
 *
 */
package com.wanci.ncc.ipmgt;

import java.util.Iterator;

/**
 * @author pg
 * @version $Id: Range.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 * 
 * object which holds several information about the range to scan
 * it holds an iterator which gives back a string with an host's IP Address
 */
public class Range implements Iterator {

	/**
     * 
	 */
    private String range = null;

	/**
     * 
	 */
    private int numberOfHosts = 0;

	/**
	 * host to start with 
	 */
	private int[] startHost = new int[4];

	/**
	 * host to end with
	 */
	private int[] endHost = new int[4];

	/**
	 * the current host where we are now
	 */
	private int[] currentHost = new int[4];

	/**
	 * helper variable for iterator
	 */
	private boolean iteratorStarted = false;

	/**
	 * initialize the iterator with different styles of network range definition
	 * you can start this class with
	 * 1: 10.48.0.1-10.48.30.1 or
	 * 2: 10.48.0.0/255.255.0.0 or
	 * 3: 10.48.0.0/16
	 * @param range
	 */
	public Range(String range) {
		this.range = range.trim();
		analyze();
	}

	/**
     * 
	 * @param network
	 * @param startIp
	 * @param stopIp
	 */
    //public Range(String network, int startIp, int stopIp) {
	//}

	/**
	 * address conversion
	 * @param 4 single numbers, e.g. (192, 168, 10, 1)
	 * @return address string e.g. "192.168.10.1"
	 */
	public String addressToString(int[] address) {
		return address[0]
			+ "."
			+ address[1]
			+ "."
			+ address[2]
			+ "."
			+ address[3];
	}

	/**
	 * address conversion
	 * @param address string e.g. "192.168.10.1"
	 * @return 4 single numbers, e.g. (192, 168, 10, 1)
	 */
	public int[] addressToBytes(String str) {

		int[] bytes = new int[4];
		String[] strs = new String[4];

		strs = str.split("\\.");
		for (int i = 0; i < strs.length; i++) {
			bytes[i] = Integer.parseInt(strs[i]);
		}

		return bytes;

	}

	/**
	 * Analysiert die Variable "range"
	 *
	 */
	private void analyze() {

		int sep;
		int index;

		//determine which type of entry here we have 
		index = range.indexOf(45);
		if (index == -1)
			index = range.indexOf(47);

		sep = range.charAt(index);

		// - sign
		if (sep == 45) {
			//case 1
			startHost = addressToBytes(range.substring(0, index));
			endHost = addressToBytes(range.substring(index + 1));
			numberOfHosts =
				endHost[3]
					+ (endHost[2] * 0x100)
					+ (endHost[1] * 0x10000)
					+ (endHost[0] * 0x1000000)
					- startHost[3]
					- (startHost[2] * 0x100)
					- (startHost[1] * 0x10000)
					- (startHost[0] * 0x1000000)
					+ 1;
		}

		// / sign
		if (sep == 47) {

			String temp1 = range.substring(0, index);
			String temp2 = range.substring(index + 1);

			int cidr = 0;
			if (temp2.length() > 2) {
				//case 2
				//temp 2 is subnet mask, convert it to cidr-value
				int[] t = addressToBytes(temp2);

				for (int i = 0; i < t.length; i++) {

					int b = t[i];
					for (int j = 7; j >= 0; j--) {

						int p = (int) Math.pow(2, j);

						b = b - p;
						if (b >= 0)
							cidr++;

					}

				}

			}
			else
				cidr = Integer.parseInt(temp2);

			//case 3
			numberOfHosts = (int) Math.pow(2, (32 - cidr)) - 1;

			startHost = addressToBytes(temp1);
			endHost = copyAddress(startHost);
			endHost = hostPlus(endHost, numberOfHosts);

		}

		currentHost = copyAddress(startHost);

	}

	/**
     * 
	 * @return
	 */
    public int getNumberOfHosts() {
		return numberOfHosts;
	}

	/**
	 * normal has next operation
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {

		if (currentHostBiggerThan(endHost))
			return false;
		else
			return true;

	}

	/**
     * 
	 * @param parts
	 * @return
	 */
    public boolean currentHostBiggerThan(int[] parts) {

		if (parts[0] > currentHost[0])
			return false;
		if (parts[1] > currentHost[1])
			return false;
		if (parts[2] > currentHost[2])
			return false;
		if (parts[3] > currentHost[3])
			return false;

		return true;

	}

	/**
     * 
	 * @return
	 */
    public String getCurrentHost() {
		return addressToString(currentHost);
	}

	/**
	 * get next object
	 */
	public Object next() {
		if (!iteratorStarted)
			iteratorStarted = true;
		else
			currentHost = computeNextHost(currentHost);
		//		while (currentHost[3] == 0 || currentHost[3] == 0xff)
		//			currentHost = computeNextHost(currentHost);

		return addressToString(currentHost);
	}

	/**
	 * ??? what to do here? i heard it is optional so i do nothing here
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
	}

	/**
	 * getter for host to end the scan with
	 * @return address, int array coded
	 */
	public int[] getEndHost() {
		return endHost;
	}

	/**
	 * getter for host to begin the scan with
	 * @return intarray coded address
	 */
	public int[] getStartHost() {
		return startHost;
	}

	/**
	 * setter for end host
	 * @param intarray coded address
	 */
	public void setEndHost(int[] is) {
		endHost = is;
	}

	/**
	 * setter for start host
	 * @param intarray coded address
	 */
	public void setStartHost(int[] is) {
		startHost = is;
	}

	/**
	 * compute the next host
	 * @param parts
	 * @return
	 */
	private int[] computeNextHost(int[] parts) {

		parts[3]++;

		if (parts[3] == 0x100) {
			parts[3] = 0;

			parts[2]++;
			if (parts[2] == 0x100) {
				parts[2] = 0;

				parts[1]++;
				if (parts[1] == 0x100) {
					parts[1] = 0;

					parts[0]++;
					if (parts[0] == 0x100) {
						return parts;
					}

				}

			}

		}

		return parts;

	}

	/**
	 * compute the host + x addresses
	 * @param parts
	 * @return intarray coded address of next host to scan
	 */
	public int[] hostPlus(int[] parts, int x) {

		int ip;
		ip =
			parts[3]
				+ (parts[2] * 0x100)
				+ (parts[1] * 0x10000)
				+ (parts[0] * 0x1000000);

		ip = ip + x;

		parts[0] = (int) Math.floor(ip / 0x1000000);
		ip = ip - (parts[0] * 0x1000000);
		parts[1] = (int) Math.floor(ip / 0x10000);
		ip = ip - (parts[1] * 0x10000);
		parts[2] = (int) Math.floor(ip / 0x100);
		ip = ip - (parts[2] * 0x100);
		parts[3] = ip;
		return parts;
	}

	/**
	 * compute the next host
	 * @param parts
	 * @return
	 */
	private int[] copyAddress(int[] parts) {

		int[] temp = new int[4];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = parts[i];
		}
		return temp;
	}

	/**
     * 
	 * @param args
	 */
    public static void main(String[] args) {
		String range = "10.40.38.0-10.40.38.255";
		//String range = "10.40.38.0/255.255.255.0";
		//String range = "0.0.0.0/24";

		Range test = new Range(range);
		while (test.hasNext()) {
			test.next();
			//System.out.println(test.getCurrentHost());
		}
		System.out.println(test.addressToString(test.getStartHost()));
		System.out.println(test.addressToString(test.getEndHost()));
		System.out.println(test.getNumberOfHosts());

	}

}