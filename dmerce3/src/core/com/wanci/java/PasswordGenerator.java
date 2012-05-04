/*
 * Created on Nov 17, 2003
 *
 */
package com.wanci.java;

import java.util.Random;

/**
 * @author rb
 * @version ${Id}
 * Password Generator
 */
public class PasswordGenerator {

	/**
	 * Strength of password
	 * L = low, M = medium, H = high
	 */
	char strength;

	/**
	 * Length of password to generate
	 */
	int length;

	Random r = new Random();
	
	public static final PasswordGenerator INSTANCE = new PasswordGenerator('L', 8);

	/**
	 * Constructor
	 * Strength should be L, M or H.
	 */
	private PasswordGenerator(char strength, int length) {
		this.strength = Character.toUpperCase(strength);
		this.length = length;
		generate();
	}
	
	public static PasswordGenerator getInstance() {
		return INSTANCE;
	}

	/**
	 * 
	 *
	 */
	StringBuffer generate() {

		StringBuffer newPassword = new StringBuffer();
		for (int i = 0; i < length; i++) {

			char c = Character.forDigit(r.nextInt(36), Character.MAX_RADIX);

			if (r.nextInt(2) > 0)
				newPassword.append(Character.toUpperCase(c));
			else
				newPassword.append(c);

		}
		
		String n = newPassword.toString();
		n = n.replace('0', '3');
		n = n.replace('1', '2');
		n = n.replace('l', '2');
		n = n.replace('O', '3');
		
		return newPassword;

	}

	/**
	 * Returns a new generated password
	 *
	 */
	public String get() {
		return generate().toString();
	}

	public static void main(String[] args) {

		for (int i = 0; i < 20; i++) {
			String p = new PasswordGenerator('L', 8).get();
			System.out.println("'" + p + "' length=" + p.length());
		}

	}

}