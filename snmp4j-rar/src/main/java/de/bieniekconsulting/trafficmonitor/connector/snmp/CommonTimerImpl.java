/**
 * 
 */
package de.bieniekconsulting.trafficmonitor.connector.snmp;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.snmp4j.util.CommonTimer;

/**
 * Wrapper facade to bridge from org.snmp4j.util.CommonTimer to java.util.Timer
 * 
 * @author rainer
 *
 * @see CommonTimer
 * @see Timer
 */
public class CommonTimerImpl implements CommonTimer {
	
	private Timer timer;
	
	CommonTimerImpl(Timer timer) {
		this.timer = timer;
	}

	/* (non-Javadoc)
	 * @see org.snmp4j.util.CommonTimer#schedule(java.util.TimerTask, long)
	 */
	@Override
	public void schedule(TimerTask task, long delay) {
		timer.schedule(task, delay);
	}

	/* (non-Javadoc)
	 * @see org.snmp4j.util.CommonTimer#schedule(java.util.TimerTask, java.util.Date, long)
	 */
	@Override
	public void schedule(TimerTask task, Date firstTime, long period) {
		timer.scheduleAtFixedRate(task, firstTime, period);
	}

	/* (non-Javadoc)
	 * @see org.snmp4j.util.CommonTimer#schedule(java.util.TimerTask, long, long)
	 */
	@Override
	public void schedule(TimerTask task, long delay, long period) {
		timer.schedule(task, delay, period);
	}

	/* (non-Javadoc)
	 * @see org.snmp4j.util.CommonTimer#cancel()
	 */
	@Override
	public void cancel() {
		timer.cancel();
	}

}
