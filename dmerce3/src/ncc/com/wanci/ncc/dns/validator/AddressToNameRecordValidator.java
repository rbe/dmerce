/*
 * AddressToNameRecordValidator.java
 *
 * Created on September 30, 2003, 2:30 PM
 */

package com.wanci.ncc.dns.validator;

import java.util.regex.Pattern;

import com.wanci.dmerce.exceptions.ValidatorException;
import com.wanci.ncc.dns.NameToAddressRecord;

/**
 *
 * @author  rb
 */
public class AddressToNameRecordValidator implements Validator {

	private NameToAddressRecord addrToNameRecord;

	/** Creates a new instance of ARecordValidator */
	public AddressToNameRecordValidator() {
	}

	/** Creates a new instance of ARecordValidator */
	public AddressToNameRecordValidator(NameToAddressRecord addrToNameRecord) {
		this.addrToNameRecord = addrToNameRecord;
	}

	private void validateName(String name) throws ValidatorException {

		// no name given
		if (name == null)
			throw new ValidatorException("A record has no name");
		else {
			// check name
			if (!Pattern.matches("\\S+|\\@", name))
				throw new ValidatorException(
					"Name '" + name + "' of A-record is not a valid name");
		}

	}

	private void validateValue(String value) throws ValidatorException {

		// no value given
		if (value == null)
			throw new ValidatorException("A record has no value");
		else {
			// check value for an ipv4 address: x[xx].x[xx].x[xx].x[xx]
			if (!Pattern
				.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", value))
				throw new ValidatorException(
					"Value '"
						+ value
						+ "' of A-record is not a valid ip address");
		}

	}

	/**
	 * Validate an A resource record
	 *
	 * The value must meet one or more of the following criteria:
	 *
	 * - value is not empty
	 * - value is NOT a name
	 * - value is an IP v4 address
	 *
	 * @throws com.wanci.dmerce.exceptions.ValidatorException
	 */
	public void validate() throws ValidatorException {

		// null object
		if (addrToNameRecord == null)
			throw new ValidatorException("No valid a record object (is null)");

		validateName(addrToNameRecord.getName());
		validateValue(addrToNameRecord.getValue());

	}

	public static void main(String[] args) throws Exception {

		AddressToNameRecordValidator a =
			new AddressToNameRecordValidator(
				new NameToAddressRecord("www", "192.168.1.1"));
		a.validate();

	}

}