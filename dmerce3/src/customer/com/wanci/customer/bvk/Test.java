/*
 * Created on Jul 28, 2003
 *
 */
package com.wanci.customer.bvk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author rb
 * @version $Id: Test.java,v 1.1 2003/11/10 19:25:31 rb Exp $
 *
 * 
 * 
 */
public class Test {

	public static void main(String[] args) throws SQLException {

		try {
			Database jdbcDatabase =
				DatabaseHandler.getDatabaseConnection("bvk");
			jdbcDatabase.openConnection();
			PreparedStatement pstmt =
				jdbcDatabase.getPreparedStatement(
					"UPDATE Member SET BVKName2 = ?, PoBox = ?, Country = ?,"
						+ " BVKMatch = ?, ZipCode = ?, BankCode = ?, City = ?,"
						+ " URL = ?, Email = ?, Phone = ?, BezirksverbandID = ?,"
						+ " Fax = ?, Traderegister = ?, Pensioner = ?, Owner2 = ?,"
						+ " Fax2 = ?, FormOfAddressID = ?, AccountNo = ?, Debit = ?,"
						+ " Owner1 = ?, TerminationreasonID = ?, PensionerSince = ?,"
						+ " PoBoxZipCode = ?, ContributionGroupID = ?, MembershipEndDate = ?,"
						+ " BVKName1 = ?, SelfEmployedFrom = ?, Email2 = ?, TerminationDate = ?,"
						+ " AccountOwner = ?, TypeOfBrokerID = ?, Street = ?, Phone2 = ?,"
						+ " FormOfOrganisationID = ?, Birthday = ?, Mobile = ?,"
						+ " MembershipStartDate = ?, Bank = ?, Owner3 = ?, BVKName3 = ?,"
						+ " Branche = ? WHERE ID = ?");
			pstmt.setString(1, "BVKName2");
			pstmt.setString(2, "PO BOX");
			pstmt.setString(3, "D");
			pstmt.setString(4, "BVKMatch");
			pstmt.setString(5, "ZIP CODE");
			pstmt.setString(6, "Bank Code");
			pstmt.setString(7, "City");
			pstmt.setString(8, "http://URL");
			pstmt.setString(9, "Email");
			pstmt.setString(10, "Phone");
			pstmt.setInt(11, 24);
			pstmt.setString(12, "Fax");
			pstmt.setNull(13, Types.CHAR);
			pstmt.setNull(14, Types.CHAR);
			pstmt.setString(15, "Owner2");
			pstmt.setString(16, "Fax");
			pstmt.setInt(17, 1);
			pstmt.setString(18, "AccNo");
			pstmt.setString(19, "d");
			pstmt.setString(20, "Owner1");
			pstmt.setInt(21, 1);
			pstmt.setNull(22, Types.DATE);
			pstmt.setString(23, "POBOXZC");
			pstmt.setInt(24, 1);
			pstmt.setNull(25, Types.DATE);
			pstmt.setString(26, "BVKMatch9");
			pstmt.setNull(27, Types.DATE);
			pstmt.setString(28, "BVKMatch7");
			pstmt.setNull(29, Types.DATE);
			pstmt.setString(30, "BVKName1");
			pstmt.setNull(31, Types.DATE);
			pstmt.setString(32, "Email2");
			pstmt.setNull(33, Types.DATE);
			pstmt.setInt(34, 1);
			pstmt.setNull(35, Types.DATE);
			pstmt.setString(36, "Mobile");
			pstmt.setNull(37, Types.DATE);
			pstmt.setString(38, "Bank");
			pstmt.setString(39, "Owner3");
			pstmt.setString(40, "BVKName3");
			pstmt.setString(41, "V");
			pstmt.setInt(42, 308);
			pstmt.executeUpdate();
			jdbcDatabase.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}