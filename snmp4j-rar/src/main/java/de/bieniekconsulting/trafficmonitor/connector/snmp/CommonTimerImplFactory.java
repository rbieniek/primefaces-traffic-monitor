/**
 * 
 */
package de.bieniekconsulting.trafficmonitor.connector.snmp;

import javax.resource.spi.BootstrapContext;
import javax.resource.spi.UnavailableException;

import org.jboss.logging.Logger;
import org.snmp4j.util.CommonTimer;
import org.snmp4j.util.TimerFactory;

/**
 * Factory for org.snmp4j.util.CommonTimer instance with timers being created by the container
 * 
 * @author rainer
 *
 */
public class CommonTimerImplFactory implements TimerFactory {
	private static final Logger logger = Logger.getLogger(CommonTimerImplFactory.class);
	
	private BootstrapContext ctx;
	
	CommonTimerImplFactory(BootstrapContext ctx) {
		this.ctx = ctx;
	}
	
	/* (non-Javadoc)
	 * @see org.snmp4j.util.TimerFactory#createTimer()
	 */
	@Override
	public CommonTimer createTimer() {
		CommonTimer timer = null;
		
		try {
			timer = new CommonTimerImpl(ctx.createTimer());
		} catch(UnavailableException e) {
			logger.warn("Cannot create timer", e);
		}
		
		return timer;
	}

}
