package de.bieniekconsulting.trafficmonitor.connector.snmp;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkAdapter;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;

import org.jboss.logging.Logger;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.util.WorkerTask;

class WorkBasedWorkerTask implements WorkerTask {
	private static Logger logger = Logger.getLogger(WorkBasedWorkerTask.class);
	
	private class StateListener extends WorkAdapter {

		private boolean running = false;
		
		@Override
		public void workStarted(WorkEvent e) {
			synchronized (this) {
				running = true;
			}
		}

		@Override
		public void workCompleted(WorkEvent e) {
			synchronized (this) {
				running = false;
				notifyAll();
			}
		}

		public boolean isRunning() {
			synchronized (this) {
				return running;
				
			}
		}
	}
	
	private Work work;
	private WorkManager workManager;
	private StateListener stateListener = new StateListener();
	
	public WorkBasedWorkerTask(WorkerTask wrappedTask, WorkManager workManager) {
		this.workManager = workManager;

		work = new Work() {
			
			@Override
			public void run() {
				wrappedTask.run();
			}
			
			@Override
			public void release() {
				wrappedTask.terminate();
			}
		};
	}

	@Override
	public void run() {
		try {
			this.workManager.scheduleWork(work, SNMP4JSettings.getThreadJoinTimeout(), null, stateListener);
		} catch (WorkException e) {
			logger.warn("cannot start work entity", e);
			
			throw new RuntimeException(e);
		}
	}

	@Override
	public void terminate() {
		logger.trace("terminate");
	}
	

	@Override
	public void join() throws InterruptedException {
		if(stateListener.isRunning()) {
			synchronized (stateListener) {
				stateListener.wait(SNMP4JSettings.getThreadJoinTimeout());
			}
		}
	}

	@Override
	public void interrupt() {
		logger.trace("interrupt");
	}
	
}