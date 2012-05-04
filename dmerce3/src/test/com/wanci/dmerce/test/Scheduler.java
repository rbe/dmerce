/*
 * Scheduler.java
 *
 * Created on September 18, 2003, 5:26 PM
 */

package com.wanci.dmerce.test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import com.wanci.customer.callando.AccountPool;
import com.wanci.customer.callando.Customers;
import com.wanci.customer.callando.ProcessRequests;
import com.wanci.customer.callando.ReadAnswer;
import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * A signal handler when this application should exit
 */
class MySignalHandler implements SignalHandler {

	private boolean DEBUG = true;

	private Scheduler scheduler;

	public MySignalHandler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void handle(Signal sig) {

		LangUtil.consoleDebug(DEBUG, "Signal " + sig + " catched.");

		try {
			scheduler.deinit();
		}
		catch (SQLException e) {
		}

		LangUtil.consoleDebug(true, "STOP");
		System.exit(2);

	}

}

/**
 * Schedules two tasks that are needed for communication between
 * callando and mediaWays:
 *
 * - ProcessRequests should be run at 45 minutes before 10, 14 and 18
 * - ReadAnswer should be run 10 minutes after 10, 14 and 18
 *
 * @author  rb
 */
public class Scheduler {

	private boolean DEBUG = true;

	private XmlPropertiesReader xmlPropReader;

	private Database jdbcDatabase;

	private AccountPool accountPool;

	private Customers customers;

	private ProcessRequests processRequests;

	private ReadAnswer readAnswer;

	private int processRequestsHour1 = 9;
	private int processRequestsMin1 = 45;
	private int processRequestsHour2 = 13;
	private int processRequestsMin2 = 45;
	private int processRequestsHour3 = 17;
	private int processRequestsMin3 = 45;

	private int readAnswerHour1 = 10;
	private int readAnswerMin1 = 10;
	private int readAnswerHour2 = 14;
	private int readAnswerMin2 = 10;
	private int readAnswerHour3 = 18;
	private int readAnswerMin3 = 10;

	private Timer timer = new Timer();

	/** Creates a new instance of Scheduler */
	public Scheduler() {
	}

	private void init()
		throws DmerceException, XmlPropertiesFormatException, SQLException {

		xmlPropReader = XmlPropertiesReader.getInstance();

		jdbcDatabase = DatabaseHandler.getDatabaseConnection("callando");
		jdbcDatabase.openConnection();

		accountPool = new AccountPool(jdbcDatabase);
		accountPool.loadAccountsFromDatabase();

		Customers customers = new Customers(jdbcDatabase, accountPool);
		customers.convertTimestampsToDate();
		customers.loadCustomersFromDatabase();

		processRequests =
			new ProcessRequests(
				accountPool,
				customers,
				new ArgumentParser(new String[] {
		}));

		readAnswer = new ReadAnswer(accountPool, customers);

	}

	public void deinit() throws SQLException {
		jdbcDatabase.closeConnection();
	}

	private void schedule() {

		Calendar cal = Calendar.getInstance();

		int hour = cal.get(Calendar.HOUR_OF_DAY);

		cal.set(
			cal.get(Calendar.YEAR),
			cal.get(Calendar.MONTH),
			cal.get(Calendar.DAY_OF_MONTH),
			9,
			45);
		timer.schedule(processRequests, cal.getTime());
		LangUtil.consoleDebug(DEBUG, "Scheduled processRequests");

		/*
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		cal.get(Calendar.DAY_OF_MONTH), 13, 45);
		timer.schedule(processRequests, cal.getTime());
		 
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		cal.get(Calendar.DAY_OF_MONTH), 17, 45);
		timer.schedule(processRequests, cal.getTime());
		 
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		cal.get(Calendar.DAY_OF_MONTH), 10, 10);
		timer.schedule(readAnswer, cal.getTime());
		 
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		cal.get(Calendar.DAY_OF_MONTH), 14, 10);
		timer.schedule(readAnswer, cal.getTime());
		 
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		cal.get(Calendar.DAY_OF_MONTH), 18, 10);
		timer.schedule(readAnswer, cal.getTime());
		 */

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		final boolean DEBUG = true;

		LangUtil.consoleDebug(true, "START");

		Scheduler scheduler = new Scheduler();

		Signal.handle(new Signal("INT"), new MySignalHandler(scheduler));
		Signal.handle(new Signal("TERM"), new MySignalHandler(scheduler));
		Signal.handle(new Signal("QUIT"), new MySignalHandler(scheduler));

		scheduler.init();

		while (true) {
			scheduler.schedule();
			Thread.sleep(1000 * 60);
		}

	}

}