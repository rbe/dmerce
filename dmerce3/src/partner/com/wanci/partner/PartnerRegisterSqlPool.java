/*
 * Created on Oct 27, 2003
 *
 */
package com.wanci.partner;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.PreparedStatementsPool;

/**
 * @author rb
 * @version ${Id}
 *
 */
public class PartnerRegisterSqlPool implements PreparedStatementsPool {

	private Database jdbcDatabase;

	public PreparedStatement sNewRegistrations;

	public PartnerRegisterSqlPool(Database jdbcDatabase) throws SQLException {

		this.jdbcDatabase = jdbcDatabase;

		sNewRegistrations =
			jdbcDatabase.getPreparedStatement(
				"SELECT r.id, r.company, r.firstname, r.lastname,"
					+ " r.street, r.zipcode, r.city, r.country, r.phone,"
					+ " r.fax, r.email, r.remarks, r.tacread, pr.fullname,"
					+ " p.company as partnercompany, p.email as partneremail,"
					+ " p.phone as partnerphone"
					+ " FROM t_blpreg r, t_partners p, t_products pr"
					+ " WHERE r.todo = 1"
					+ " AND r.partnerid = p.id"
					+ " AND r.productid = pr.productkey");

	}

}
