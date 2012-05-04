/*
 * Created on 26.07.2004
 *
 */
package com.wanci.dmerce.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Messages;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: JobManager.java,v 1.1 2004/07/26 11:42:56 rb Exp $
 * 
 * Manages jobs through JobStarter; provides accesss to database tables (t_jobs)
 * for different projects
 *  
 */
public class JobManager {

	/**
	 * Database connection
	 */
	private Database jdbcDatabase;

	/**
	 * Statement to check for jobs to be run; fetch all jobs from database that
	 * have no firstexecutionat is not set or equal to now (day, month, year,
	 * hour and minute == now) and their last execution date is greater than or
	 * equal to now
	 */
	private String checkJobsStatement = "SELECT id, jobclass, jobname,"
			+ " firstexecutionat, lastexecutionat, period" + " FROM t_jobs"
			+ " WHERE firstexecutionat IS NULL"
			+ " OR TO_CHAR(firstexecutionat, 'DD.MM.YYYY HH24:MI') ="
			+ " TO_CHAR(SYSDATE, 'DD.MM.YYYY HH24:MI')"
			+ " AND TO_CHAR(lastexecutionat, 'DD.MM.YYYY HH24:MI') >="
			+ " TO_CHAR(SYSDATE, 'DD.MM.YYYY HH24:MI')";

	/**
	 * Fetch all jobs from t_jobs and creates JobDescription objects from the
	 * result. These objects are placed into a linked list
	 * 
	 * @throws SQLException
	 *  
	 */
	private void checkDatabase() throws SQLException {

		// Try to get database connection from DatabaseHandler
		// (using dmerce.properties and connection identifier "jobs")
		jdbcDatabase = DatabaseHandler.getDatabaseConnection("jobs");

		// We got a database handler
		if (jdbcDatabase != null)
			jdbcDatabase.openConnection();
		else
			LangUtil.consoleDebug(true, Messages.getErrorMessage(1, 100));

	}

	/**
	 * 
	 * @return
	 */
	private LinkedList checkJobs() {

		// List with job description objects
		LinkedList jobs = new LinkedList();

		try {

			// Query database for jobs to execute
			ResultSet rs = jdbcDatabase.executeQuery(checkJobsStatement);
			while (rs.next()) {

				// Create new job description object
				jobs.add(new JobDescription(rs.getString("jobclass"), rs
						.getString("jobname"), rs.getDate("firstexecutionat"),
						rs.getDate("lastexecutionat"), rs.getLong("period")));

			}

		} catch (SQLException e) {
		}

		return jobs;

	}

	public static void main(String[] args) {
	}

}