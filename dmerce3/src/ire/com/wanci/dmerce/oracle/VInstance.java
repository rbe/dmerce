/*
 * Created on Mar 16, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.oracle;

import java.sql.Date;
import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Oracle;

/**
 * @author rb2
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class VInstance extends ManageDataDictionary {

	/**
	 * 
	 */
	private String activeState;

	/**
	 * 
	 */
	private String instanceRole;

	/**
	 * 
	 */
	private String databaseStatus;

	/**
	 * 
	 */
	private boolean shutdownPending;

	/**
	 * 
	 */
	private String logSwitchWait;

	/**
	 * 
	 */
	private String archiver;

	/**
	 * 
	 */
	private boolean logins;

	/**
	 * 
	 */
	private int threadNumber;

	/**
	 * 
	 */
	private boolean parallel;

	/**
	 * 
	 */
	private String version;

	/**
	 * 
	 */
	private String hostName;

	/**
	 * 
	 */
	private String instanceName;

	/**
	 * 
	 */
	private int instanceNumber;

	/**
	 * 
	 */
	private Date startupTime;

	/**
	 * 
	 */
	private String status;

	/**
	 * 
	 * @param jdbcOracle
	 */
	public VInstance(Oracle jdbcOracle) {
		this.jdbcOracle = jdbcOracle;
		setStatement();
	}

	/**
	 * 
	 */
	public void discover() {
		
		super.discover();

		try {

			result.next();

			activeState = result.getString("active_state");
			archiver = result.getString("archiver");
			databaseStatus = result.getString("database_status");
			instanceNumber = result.getInt("instance_number");
			instanceName = result.getString("instance_name");
			instanceRole = result.getString("instance_role");

			if (result.getString("logins").equalsIgnoreCase("allowed"))
				logins = true;
			else
				logins = false;

			logSwitchWait = result.getString("log_switch_wait");

			if (result.getString("parallel").equalsIgnoreCase("yes"))
				parallel = true;
			else
				parallel = false;

			if (result.getString("shutdown_pending").equalsIgnoreCase("yes"))
				shutdownPending = true;
			else
				shutdownPending = false;

			startupTime = result.getDate("startup_time");
			status = result.getString("status");
			threadNumber = result.getInt("thread#");
			version = result.getString("version");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return
	 */
	public String getActiveState() {
		return activeState;
	}

	/**
	 * 
	 * @return
	 */
	public String getArchiver() {
		return archiver;
	}

	/**
	 * 
	 * @return
	 */
	public String getDatabaseStatus() {
		return databaseStatus;
	}

	/**
	 * 
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * 
	 * @return
	 */
	public int getInstanceNumber() {
		return instanceNumber;
	}

	/**
	 * 
	 * @return
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * 
	 * @return
	 */
	public String getInstanceRole() {
		return instanceRole;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getLogins() {
		return logins;
	}

	/**
	 * 
	 * @return
	 */
	public String getLogSwitchWait() {
		return logSwitchWait;
	}

	/**
	 * 
	 * @return
	 */
	public Date getStartupTime() {
		return startupTime;
	}

	/**
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @return
	 */
	public int getThreadNumber() {
		return threadNumber;
	}

	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isArchiverStarted() {
		if (archiver.equalsIgnoreCase("started"))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isArchiverStopped() {
		if (archiver.equalsIgnoreCase("stopped"))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDatabaseActive() {
		if (databaseStatus.equalsIgnoreCase("active"))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isLoginAllowed() {
		if (logins)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isShutdownPending() {
		return shutdownPending;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isParallel() {
		return parallel;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionary#recreate()
	 */
	public String recreate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionary#setStatement(java.lang.String)
	 * 
	 *  INSTANCE_NUMBER                                    NUMBER
	 *  INSTANCE_NAME                                      VARCHAR2(16)
	 *  HOST_NAME                                          VARCHAR2(64)
	 *  VERSION                                            VARCHAR2(17)
	 *  STARTUP_TIME                                       DATE
	 *  STATUS                                             VARCHAR2(7)
	 *  PARALLEL                                           VARCHAR2(3)
	 *  THREAD#                                            NUMBER
	 *  ARCHIVER                                           VARCHAR2(7)
	 *  LOG_SWITCH_WAIT                                    VARCHAR2(11)
	 *  LOGINS                                             VARCHAR2(10)
	 *  SHUTDOWN_PENDING                                   VARCHAR2(3)
	 *  DATABASE_STATUS                                    VARCHAR2(17)
	 *  INSTANCE_ROLE                                      VARCHAR2(18)
	 *  ACTIVE_STATE                                       VARCHAR2(9)
	 * 
	 * IN INSTANCE_NAME    HOST_NAME  VERSION    STARTUP_T STATUS  PAR    THREAD# ARCHIVE LOG_SWITCH_ LOGINS     SHU stat       INSTANCE_ROLE      ACTIVE_ST
	 * -- ---------------- ---------- ---------- --------- ------- --- ---------- ------- ----------- ---------- --- ---------- ------------------ ---------
	 *  1 messe            cini       9.0.1.0.0  14-MAR-03 OPEN    NO           1 STARTED             ALLOWED    NO  ACTIVE     PRIMARY_INSTANCE   NORMAL
	 */
	public void setStatement() {
		stmt =
			"SELECT instance_number, instance_name, host_name, version, startup_time, status,"
				+ " parallel, thread#, archiver, log_switch_wait, logins, shutdown_pending,"
				+ " database_status, instance_role, active_state"
				+ " FROM v$instance";
	}

}