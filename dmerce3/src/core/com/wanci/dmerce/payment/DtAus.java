/*
 * Created on 09.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author rb
 * @version $Id: DtAus.java,v 1.7 2004/08/04 17:33:34 rb Exp $
 *  
 */
public interface DtAus {

	void setMyCompanyName(String myCompanyName);

	void setMyFirstName(String myFirstName);

	void setMyLastName(String myLastName);

	void setMyStreet(String myStreet);

	void setMyZipCode(String myZipCode);

	void setMyCity(String myCity);

	void setBankCompanyName(String bankCompanyName);

	void setBankFirstName(String bankFirstName);

	void setBankLastName(String bankLastName);

	void setBankStreet(String bankStreet);

	void setBankZipCode(String bankZipCode);

	void setBankCity(String bankCity);

	void setBankCode(String bankCode);

	void setBankAccountNumber(String bankAccountNumber);

	void add(double sum, String name, String accountNumber, String bankCode,
			String reason) throws IOException, ParseException;

}