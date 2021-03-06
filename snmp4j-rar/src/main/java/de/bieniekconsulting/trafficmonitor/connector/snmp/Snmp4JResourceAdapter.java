/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.bieniekconsulting.trafficmonitor.connector.snmp;

import de.bieniekconsulting.trafficmonitor.connector.snmp.inflow.Snmp4JActivation;
import de.bieniekconsulting.trafficmonitor.connector.snmp.inflow.Snmp4JActivationSpec;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.CommonTimer;
import org.snmp4j.util.TimerFactory;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

/**
 * Snmp4JResourceAdapter
 *
 * @version $Revision: $
 */
@Connector(
   reauthenticationSupport = false,
   transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction)
public class Snmp4JResourceAdapter implements ResourceAdapter, java.io.Serializable
{

   /** The serial version UID */
   private static final long serialVersionUID = 1L;

   /** The logger */
   private static Logger log = Logger.getLogger(Snmp4JResourceAdapter.class.getName());

   /** The activations by activation spec */
   private ConcurrentHashMap<Snmp4JActivationSpec, Snmp4JActivation> activations;

   private WorkManager workManager;
   
   private TransportMapping<UdpAddress> transport;
   private Snmp snmp;
   
   /**
    * Default constructor
    */
   public Snmp4JResourceAdapter()
   {
      this.activations = new ConcurrentHashMap<Snmp4JActivationSpec, Snmp4JActivation>();

   }

   /**
    * This is called during the activation of a message endpoint.
    *
    * @param endpointFactory A message endpoint factory instance.
    * @param spec An activation spec JavaBean instance.
    * @throws ResourceException generic exception 
    */
   public void endpointActivation(MessageEndpointFactory endpointFactory,
      ActivationSpec spec) throws ResourceException
   {
      Snmp4JActivation activation = new Snmp4JActivation(this, endpointFactory, (Snmp4JActivationSpec)spec);
      activations.put((Snmp4JActivationSpec)spec, activation);
      activation.start();

      log.tracef("endpointActivation(%s, %s)", endpointFactory, spec);

   }

   /**
    * This is called when a message endpoint is deactivated. 
    *
    * @param endpointFactory A message endpoint factory instance.
    * @param spec An activation spec JavaBean instance.
    */
   public void endpointDeactivation(MessageEndpointFactory endpointFactory,
      ActivationSpec spec)
   {
      Snmp4JActivation activation = activations.remove(spec);
      if (activation != null)
         activation.stop();

      log.tracef("endpointDeactivation(%s)", endpointFactory);

   }

   /**
    * This is called when a resource adapter instance is bootstrapped.
    *
    * @param ctx A bootstrap context containing references 
    * @throws ResourceAdapterInternalException indicates bootstrap failure.
    */
   public void start(BootstrapContext ctx) throws ResourceAdapterInternalException
   {
      log.tracef("start(%s)", ctx);

      this.workManager = ctx.getWorkManager();
      
      SNMP4JSettings.setTimerFactory(new CommonTimerImplFactory(ctx));
      SNMP4JSettings.setThreadFactory(new Snmp4JThreadFactory(ctx.getWorkManager()));
      
      try {
    	transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);

		transport.listen();
      } catch (IOException e) {
		log.error("cannot start embedded SNMP isntance", e);
			
		throw new ResourceAdapterInternalException(e);
      }
   }

   /**
    * This is called when a resource adapter instance is undeployed or
    * during application server shutdown. 
    */
   public void stop()
   {
      log.trace("stop()");

      try {
    	  transport.close();
      } catch(IOException e) {
    	  log.warn("cannot close embedded SNMP transport", e);
      }
      this.workManager = null;
   }

   /**
    * This method is called by the application server during crash recovery.
    *
    * @param specs An array of ActivationSpec JavaBeans 
    * @throws ResourceException generic exception 
    * @return An array of XAResource objects
    */
   public XAResource[] getXAResources(ActivationSpec[] specs)
      throws ResourceException
   {
      log.tracef("getXAResources(%s)", specs.toString());
      return null;
   }

   /**
    * This method returns the current work manager.
    * 
    * @return
    */
   public WorkManager getWorkManager() {
	   return workManager;
   }

   /** 
    * Returns a hash code value for the object.
    * @return A hash code value for this object.
    */
   @Override
   public int hashCode()
   {
      int result = 17;
      return result;
   }

   /** 
    * Indicates whether some other object is equal to this one.
    * @param other The reference object with which to compare.
    * @return true if this object is the same as the obj argument, false otherwise.
    */
   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (other == this)
         return true;
      if (!(other instanceof Snmp4JResourceAdapter))
         return false;
      boolean result = true;
      return result;
   }

/**
 * @return the snmp
 */
public Snmp getSnmp() {
	return snmp;
}

}
