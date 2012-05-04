/*
 * Created on Aug 9, 2004
 *
 */
package com.wanci.customer.bvk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author rb
 * @version $Id: CorrectMemberEducationProfiles.java,v 1.1 2004/08/10 15:27:12
 *          rb Exp $
 *  
 */
public class CorrectMemberEducationProfiles {

	/**
	 * Database connection
	 */
	private Database jdbcDatabase;

	/**
	 * Constructor
	 * 
	 * @param jdbcDatabase
	 */
	public CorrectMemberEducationProfiles(Database jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	/**
	 * Go for Gold!
	 * 
	 * @throws SQLException
	 */
	public void go() throws SQLException {

		jdbcDatabase.openConnection();

		// Query all members that have more than 3 entries in
		// membereducationprofile
		ResultSet rs = jdbcDatabase
				.executeQuery("SELECT c, memberid FROM v_member_w_eduprofile_gt3");
		int memberId;
		while (rs.next()) {

			int numberOfProfileIDs = 0;
			memberId = rs.getInt("memberid");
			// Vector for checking educationprofileids
			Vector eduprofileids = new Vector();

			// Query all entries in membereducationprofile and check them:
			// - every "educationprofileid" must occur only once
			// - delete every entry except first three
			ResultSet rs2 = jdbcDatabase
					.executeQuery("SELECT id, educationprofileid"
							+ " FROM membereducationprofile"
							+ " WHERE memberid = " + memberId);
			while (rs2.next()) {

				int ep = rs2.getInt("educationprofileid");
				Integer epi = new Integer(ep);
				numberOfProfileIDs++;

				// If not already three educationprofileids are known and
				// educationprofileid is not already known, add it to vector
				// of ids; else delete entry from database
				if (!eduprofileids.contains(epi) && numberOfProfileIDs <= 3) {

					eduprofileids.add(epi);

					System.out.println("+ member #" + memberId + " profile #"
							+ numberOfProfileIDs + " #" + epi);

				} else {

					System.out.println("- member #" + memberId + " profile #"
							+ numberOfProfileIDs + " #" + epi);

					jdbcDatabase
							.executeUpdate("DELETE FROM membereducationprofile"
									+ " WHERE id = " + rs2.getInt("id"));

				}

			}

		}

		jdbcDatabase.closeConnection();

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		CorrectMemberEducationProfiles cmep = new CorrectMemberEducationProfiles(
				DatabaseHandler.getDatabaseConnection("bvk"));
		cmep.go();

	}

}