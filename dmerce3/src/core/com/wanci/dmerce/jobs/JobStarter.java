/*
 * Created on Jul 23, 2004
 *
 */
package com.wanci.dmerce.jobs;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: JobStarter.java,v 1.1 2004/07/26 11:42:56 rb Exp $
 * 
 * Job description object
 *  
 */
class JobDescription {

	/**
	 * Job class: can be "java"
	 */
	String jobClass;

	/**
	 * Name of the job to execute
	 * 
	 * When job class is "java" the name is a class name
	 */
	String jobName;

	/**
	 * First date of execution of this jobs; can be null
	 */
	Date firstExecutionDate;

	/**
	 * Last date of execution of this jobs; can be null
	 */
	Date lastExecutionDate;

	/**
	 * Period
	 */
	long period;

	/**
	 * 
	 * @param jobClass
	 * @param jobName
	 * @param firstExecutionDate
	 * @param lastExecutionDate
	 * @param period
	 */
	JobDescription(String jobClass, String jobName, Date firstExecutionDate,
			Date lastExecutionDate, long period) {

		this.jobClass = jobClass;
		this.jobName = jobName;
		this.firstExecutionDate = firstExecutionDate;
		this.lastExecutionDate = lastExecutionDate;
		this.period = period;

	}

}

/**
 * @author rb
 * @version $Id: JobStarter.java,v 1.1 2004/07/26 11:42:56 rb Exp $
 * 
 * Execute a java job; a class that implements com.wanci.dmerce.kernel.Job
 *  
 */

class JavaJobExecutor {

	JobDescription jobDescription;

	Class jobClass;

	Job job;

	Timer timer = new Timer();

	/**
	 * Create a job executor object for java jobs.
	 * 
	 * @param jobDescription
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	JavaJobExecutor(JobDescription jobDescription)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		this.jobDescription = jobDescription;
		jobClass = Class.forName(jobDescription.jobName);

		boolean implementsJob = false;

		// Check if class implements interface com.wanci.dmerce.jobs.Job
		Class[] interfaces = jobClass.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {

			System.out.println(interfaces[i].getName());

			if (interfaces[i].getName().equals("com.wanci.dmerce.jobs.Job"))
				implementsJob = true;

		}

		if (implementsJob) {

			Job job = (Job) jobClass.newInstance();

			timer.schedule((TimerTask) job, jobDescription.firstExecutionDate,
					jobDescription.period);

		}

	}

}

/**
 * @author rb
 * @version $Id: JobStarter.java,v 1.1 2004/07/26 11:42:56 rb Exp $
 * 
 * Periodically check database table t_jobs to run jobs. <br>
 * Table t_jobs is defined as:
 * <p>
 * id - NUMBER - a unique ID for a job description <br>
 * jobclass - VARCHAR - class of job: java <br>
 * jobname - VARCHAR - name of job to execute <br>
 * firstexecutionat - DATE - date when job is executed for the first time <br>
 * lastexecutionat - DATE - date when job is executed for the last time (or null
 * for never) <br>
 * lastexecutedat - DATE - date when job was last executed <br>
 * 
 * <p>
 * Every time when jobstarter wakes up it retrieves all job descriptions from
 * the database and checks for new jobs (.filterJobs())
 * 
 * TODO: job class "shell" <br>
 * TODO: entries in properties.xml for more than one database connection (job
 * starter should watch in differents databases/schemas/projects on
 * t_jobs-tables)
 *  
 */

public class JobStarter extends TimerTask {

	/**
	 * Should jobs be executed?
	 */
	private boolean enabled = false;

	/**
	 * Timer for execution of JobStarter
	 */
	private Timer timer = new Timer();

	/**
	 * All job descriptions of jobs known to JobStarter
	 */
	private Vector jobDescriptions = new Vector();

	/**
	 * All job timers of jobs know to JobStarter
	 */
	private Vector jobTimers = new Vector();

	/**
	 * Constructor
	 * 
	 * Creates a timer that wakes up every minute
	 *  
	 */
	public JobStarter() {
		timer.schedule(this, 0, 60 * 1000);
	}

	/**
	 * 
	 * @param jobs
	 */
	public void addJobs(LinkedList jobs) {
		jobDescriptions.addAll(filterJobs(jobs));
	}

	/**
	 * Compare jobs with jobs known to us and return new jobs
	 * 
	 * @param jobs
	 * @return
	 */
	private LinkedList filterJobs(LinkedList jobs) {

		LinkedList newJobs = new LinkedList();

		Iterator jobsIterator = jobs.iterator();
		while (jobsIterator.hasNext()) {

			JobDescription newJob = (JobDescription) jobsIterator.next();

			Iterator knownJobsIterator = jobDescriptions.iterator();
			while (knownJobsIterator.hasNext()) {

				JobDescription knownJob = (JobDescription) knownJobsIterator
						.next();

				// Compare job class and job name
				if (newJob.jobClass.equals(knownJob.jobClass)
						&& newJob.jobName.equals(knownJob.jobName))
					newJobs.add(newJob);

			}

		}

		return newJobs;

	}

	/**
	 * Start new jobs
	 * 
	 * @param jobs
	 */
	private void runJobs(LinkedList jobs) {

		Iterator jobsIterator = jobs.iterator();
		while (jobsIterator.hasNext()) {

			JobDescription j = (JobDescription) jobsIterator.next();
			if (j.jobName.equals("java")) {

				try {
					jobTimers.add(new JavaJobExecutor(j));
				} catch (ClassNotFoundException e) {

					LangUtil.consoleDebug(true, "Cannot execute Java-job '"
							+ j.jobName + "': cannot find class in classpath "
							+ System.getProperty("java.class.path"));

				} catch (InstantiationException e) {

					LangUtil.consoleDebug(true, "Cannot execute Java-job '"
							+ j.jobName
							+ "': cannot create an instance of class");

				} catch (IllegalAccessException e) {

					LangUtil.consoleDebug(true, "Cannot execute Java-job '"
							+ j.jobName + "': " + e.getMessage());

				}

			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	public void run() {

		if (enabled) {
			;
		} else
			LangUtil.consoleDebug(true, "JobStarter is disabled");

	}

	/**
	 * Set flag if job starter is enabled or not
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void start() {
		setEnabled(true);
	}

	public void stop() {
		setEnabled(false);
	}

	public static void main(String[] args) {
		JobStarter j = new JobStarter();
		j.setEnabled(true);
	}

}