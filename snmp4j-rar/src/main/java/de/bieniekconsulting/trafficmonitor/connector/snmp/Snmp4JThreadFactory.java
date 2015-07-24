/**
 * 
 */
package de.bieniekconsulting.trafficmonitor.connector.snmp;

import javax.resource.spi.work.WorkManager;

import org.snmp4j.util.ThreadFactory;
import org.snmp4j.util.WorkerTask;

/**
 * @author rainer
 *
 */
public class Snmp4JThreadFactory implements ThreadFactory {

	private WorkManager workManager;

	public Snmp4JThreadFactory(WorkManager workManager) {
		this.workManager = workManager;
	}
	
	/* (non-Javadoc)
	 * @see org.snmp4j.util.ThreadFactory#createWorkerThread(java.lang.String, org.snmp4j.util.WorkerTask, boolean)
	 */
	@Override
	public WorkerTask createWorkerThread(String name, WorkerTask task, boolean daemon) {
		return new WorkBasedWorkerTask(task, workManager);
	}
}
