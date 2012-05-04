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
public class VDatabase extends ManageDataDictionary {

	/**
	 * 
	 */
	private int dbid;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private Date created;

	/**
	 * 
	 */
	private String logMode;

	/**
	 * 
	 */
	private boolean archiveLogMode;

	/**
	 * 
	 */
	private Date controlFileCreated;

	/**
	 * 
	 */
	private Date controlFileTime;

	/**
	 * 
	 */
	private String openResetLogsString;

	/**
	 * 
	 */
	private boolean openResetLogs;

	/**
	 * 
	 */
	private String openMode;

	/**
	 * 
	 */
	private String standbyMode;

	/**
	 * 
	 */
	private String remoteArchive;

	/**
	 * 
	 * @param jdbcOracle
	 */
	public VDatabase(Oracle jdbcOracle) {
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

			dbid = result.getInt("dbid");
			name = result.getString("name");
			created = result.getDate("created");

			logMode = result.getString("log_mode");
			if (logMode.equalsIgnoreCase("archivelog"))
				archiveLogMode = true;
			else
				archiveLogMode = false;

			controlFileCreated = result.getDate("controlfile_created");
			controlFileTime = result.getDate("controlfile_time");

			openResetLogsString = result.getString("open_resetlogs");
			if (openResetLogsString.equalsIgnoreCase("allowed"))
				openResetLogs = true;
			else
				openResetLogs = false;

			openMode = result.getString("open_mode");
			standbyMode = result.getString("standby_mode");
			remoteArchive = result.getString("remote_archive");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return Date
	 */
	public Date getControlFileCreated() {
		return controlFileCreated;
	}

	/**
	 * @return Date
	 */
	public Date getControlFileTime() {
		return controlFileTime;
	}

	/**
	 * @return Date
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @return int
	 */
	public int getDbid() {
		return dbid;
	}

	/**
	 * @return String
	 */
	public String getLogMode() {
		return logMode;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return String
	 */
	public String getOpenMode() {
		return openMode;
	}

	/**
	 * @return String
	 */
	public boolean isOpenResetLogs() {
		return openResetLogs;
	}
	
	/**
	 * @return String
	 */
	public String getRemoteArchive() {
		return remoteArchive;
	}

	/**
	 * @return String
	 */
	public String getStandbyMode() {
		return standbyMode;
	}

	public boolean isArchiveLogMode() {
		return archiveLogMode;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.oracle.ManageOracleDataDictionary#recreate()
	 */
	public String recreate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.oracle.ManageOracleDataDictionary#setStatement()
	 *
		 * SQL> desc v$database;
		 * Name                                                                                  Null?    Type
		 * ------------------------------------------------------------------------------------- -------- ----------------------------------------------------------
		 * DBID                                                                                           NUMBER
		 * NAME                                                                                           VARCHAR2(9)
		 * CREATED                                                                                        DATE
		 * RESETLOGS_CHANGE#                                                                              NUMBER
		 * RESETLOGS_TIME                                                                                 DATE
		 * PRIOR_RESETLOGS_CHANGE#                                                                        NUMBER
		 * PRIOR_RESETLOGS_TIME                                                                           DATE
		 * LOG_MODE                                                                                       VARCHAR2(12)
		 * CHECKPOINT_CHANGE#                                                                             NUMBER
		 * ARCHIVE_CHANGE#                                                                                NUMBER
		 * CONTROLFILE_TYPE                                                                               VARCHAR2(7)
		 * CONTROLFILE_CREATED                                                                            DATE
		 * CONTROLFILE_SEQUENCE#                                                                          NUMBER
		 * CONTROLFILE_CHANGE#                                                                            NUMBER
		 * CONTROLFILE_TIME                                                                               DATE
		 * OPEN_RESETLOGS                                                                                 VARCHAR2(11)
		 * VERSION_TIME                                                                                   DATE
		 * OPEN_MODE                                                                                      VARCHAR2(10)
		 * STANDBY_MODE                                                                                   VARCHAR2(11)
		 * REMOTE_ARCHIVE                                                                                 VARCHAR2(11)
		 * ACTIVATION#                                                                                    NUMBER
		 * DATABASE_ROLE                                                                                  VARCHAR2(16)
		 * ARCHIVELOG_CHANGE#                                                                             NUMBER
		 * SWITCHOVER_STATUS                                                                              VARCHAR2(18)
		 * SQL> select * from v$database;
		 * DBID       NAME      CREATED   RESETLOGS_CHANGE# RESETLOGS PRIOR_RESETLOGS_CHANGE# PRIOR_RES LOG_MODE     CHECKPOINT_CHANGE# ARCHIVE_CHANGE# CONTROL CONTROLFI CONTROLFILE_SEQUENCE# CONTROLFILE_CHANGE# CONTROLFI OPEN_RESETL VERSION_T OPEN_MODE  STANDBY_MOD REMOTE_ARCH ACTIVATION# DATABASE_ROLE    ARCHIVELOG_CHANGE# SWITCHOVER_STATUS
		 * ---------- --------- --------- ----------------- --------- ----------------------- --------- ------------ ------------------ --------------- ------- --------- --------------------- ------------------- --------- ----------- --------- ---------- ----------- ----------- ----------- ---------------- ------------------ ------------------
		 * 1269458105 MESSE     02-MAR-03                 1 02-MAR-03                       0           ARCHIVELOG               227636          170728 CURRENT 02-MAR-03                   184              227636 16-MAR-03 NOT ALLOWED 02-MAR-03 READ WRITE UNPROTECTED ENABLED      1269447865 PRIMARY                      223730 NOT ALLOWED
		 */
	public void setStatement() {
		stmt =
			"SELECT dbid, name, created, log_mode, controlfile_created, controlfile_time, open_resetlogs,"
				+ " open_mode, standby_mode, remote_archive"
				+ " FROM v$database";
	}

}