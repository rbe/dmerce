/**
 * Created on Feb 17, 2003
 *
 */
package com.wanci.dmerce.oracle;

import java.sql.SQLException;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Oracle;

/**
 * @author rb
 * @version $Id: DataDictionaryViews.java,v 1.1 2004/03/29 13:39:37 rb Exp $
 *
 */
public class DataDictionaryViews {

	public DataDictionaryViews() {
	}
	
	private static void e(Object o) {
		System.out.println(o);
	}

	private static void e(int i) {
		System.out.println(i);
	}

	private static void e(boolean b) {
		System.out.println(b);
	}

	public static void main(String[] args) {

		try {

			Oracle oracle =
				new Oracle(
					"jdbc:oracle:thin:system/manager@10.48.35.3:1521:wanci2",
					"system",
					"manager");

			oracle.openConnection();

			e("v$instance");
			e("");
			VInstance di = new VInstance(oracle);
			di.discover();
			e(di.getActiveState());
			e(di.getArchiver());
			e(di.getDatabaseStatus());
			e(di.getHostName());
			e(di.getInstanceName());
			e(di.getInstanceRole());
			e(di.getInstanceNumber());
			e(di.getLogSwitchWait());
			e(di.isLoginAllowed());
			e(di.getStartupTime());
			e(di.getStatus());

			e("");
			e("");

			e("v$database");
			e("");
			VDatabase dd = new VDatabase(oracle);
			dd.discover();
			e(dd.getDbid());
			e(dd.getName());
			e(dd.getCreated());
			e(dd.getLogMode());
			e(dd.getControlFileCreated());
			e(dd.getControlFileTime());
			e(dd.isOpenResetLogs());
			e(dd.getOpenMode());
			e(dd.getStandbyMode());
			e(dd.getRemoteArchive());

			oracle.closeConnection();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DmerceException e) {
			e.printStackTrace();
		}

	}

}