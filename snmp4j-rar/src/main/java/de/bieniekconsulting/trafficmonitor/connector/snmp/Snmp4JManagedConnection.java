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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.logging.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import de.bieniekconsulting.trafficmonitor.data.snmp.SystemInfo;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.resource.spi.work.WorkManager;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

/**
 * Snmp4JManagedConnection
 *
 * @version $Revision: $
 */
public class Snmp4JManagedConnection implements ManagedConnection {

   static final OID OID_sys = new OID(new int[] {1, 3, 6, 1, 2, 1, 1});
   static final OID OID_sysORLastChange = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 8});
   static final OID OID_sysServices = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 7});
   static final OID OID_sysLocation = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 6});
   static final OID OID_sysName = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 5});
   static final OID OID_sysContact = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 4});
   static final OID OID_sysUpTime = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 3});
   static final OID OID_sysObjectID = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 2});
   static final OID OID_sysDescr = new OID(new int[] {1, 3, 6, 1, 2, 1, 1, 1});

/** The logger */
   private static Logger log = Logger.getLogger(Snmp4JManagedConnection.class.getName());

   /** The logwriter */
   private PrintWriter logwriter;

   /** ManagedConnectionFactory */
   private Snmp4JManagedConnectionFactory mcf;

   /** Listeners */
   private List<ConnectionEventListener> listeners;

   /** Connection */
   private Snmp4JConnectionImpl connection;

   private Snmp4JConnectionRequestInfo requestInfo;

   private Subject subject;
   
   private Snmp snmp;
   
   /**
    * Default constructor
    * @param mcf mcf
    * @param subject 
    * @param cxRequestInfo 
    * @param workManager 
    */
   public Snmp4JManagedConnection(Snmp4JManagedConnectionFactory mcf, Subject subject, Snmp4JConnectionRequestInfo cxRequestInfo, Snmp snmp)
   {
      this.mcf = mcf;
      this.logwriter = null;
      this.listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>(1));
      this.connection = null;
      this.requestInfo = cxRequestInfo;
      this.subject = subject;
      this.snmp = snmp;
   }

   /**
    * Creates a new connection handle for the underlying physical connection 
    * represented by the ManagedConnection instance. 
    *
    * @param subject Security context as JAAS subject
    * @param cxRequestInfo ConnectionRequestInfo instance
    * @return generic Object instance representing the connection handle. 
    * @throws ResourceException generic exception if operation fails
    */
   public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException
   {
      log.trace("getConnection()");
      connection = new Snmp4JConnectionImpl(this, mcf);
      
      return connection;
   }

   /**
    * Used by the container to change the association of an 
    * application-level connection handle with a ManagedConneciton instance.
    *
    * @param connection Application-level connection handle
    * @throws ResourceException generic exception if operation fails
    */
   public void associateConnection(Object connection) throws ResourceException
   {
      log.tracef("associateConnection(%s)", connection);

      if (connection == null)
         throw new ResourceException("Null connection handle");

      if (!(connection instanceof Snmp4JConnectionImpl))
         throw new ResourceException("Wrong connection handle");

      this.connection = (Snmp4JConnectionImpl)connection;
   }

   /**
    * Application server calls this method to force any cleanup on the ManagedConnection instance.
    *
    * @throws ResourceException generic exception if operation fails
    */
   public void cleanup() throws ResourceException
   {
      log.trace("cleanup()");

   }

   /**
    * Destroys the physical connection to the underlying resource manager.
    *
    * @throws ResourceException generic exception if operation fails
    */
   public void destroy() throws ResourceException
   {
      log.trace("destroy()");

   }

   /**
    * Adds a connection event listener to the ManagedConnection instance.
    *
    * @param listener A new ConnectionEventListener to be registered
    */
   public void addConnectionEventListener(ConnectionEventListener listener)
   {
      log.tracef("addConnectionEventListener(%s)", listener);
      if (listener == null)
         throw new IllegalArgumentException("Listener is null");
      listeners.add(listener);
   }

   /**
    * Removes an already registered connection event listener from the ManagedConnection instance.
    *
    * @param listener already registered connection event listener to be removed
    */
   public void removeConnectionEventListener(ConnectionEventListener listener)
   {
      log.tracef("removeConnectionEventListener(%s)", listener);
      if (listener == null)
         throw new IllegalArgumentException("Listener is null");
      listeners.remove(listener);
   }

   /**
    * Close handle
    *
    * @param handle The handle
    */
   void closeHandle(Snmp4JConnection handle)
   {
      ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
      event.setConnectionHandle(handle);
      for (ConnectionEventListener cel : listeners)
      {
         cel.connectionClosed(event);
      }

   }

   /**
    * Gets the log writer for this ManagedConnection instance.
    *
    * @return Character output stream associated with this Managed-Connection instance
    * @throws ResourceException generic exception if operation fails
    */
   public PrintWriter getLogWriter() throws ResourceException
   {
      log.trace("getLogWriter()");
      return logwriter;
   }

   /**
    * Sets the log writer for this ManagedConnection instance.
    *
    * @param out Character Output stream to be associated
    * @throws ResourceException  generic exception if operation fails
    */
   public void setLogWriter(PrintWriter out) throws ResourceException
   {
      log.tracef("setLogWriter(%s)", out);
      logwriter = out;
   }

   /**
    * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
    *
    * @return LocalTransaction instance
    * @throws ResourceException generic exception if operation fails
    */
   public LocalTransaction getLocalTransaction() throws ResourceException
   {
      throw new NotSupportedException("getLocalTransaction() not supported");
   }

   /**
    * Returns an <code>javax.transaction.xa.XAresource</code> instance. 
    *
    * @return XAResource instance
    * @throws ResourceException generic exception if operation fails
    */
   public XAResource getXAResource() throws ResourceException
   {
      throw new NotSupportedException("getXAResource() not supported");
   }

   /**
    * Gets the metadata information for this connection's underlying EIS resource manager instance. 
    *
    * @return ManagedConnectionMetaData instance
    * @throws ResourceException generic exception if operation fails
    */
   public ManagedConnectionMetaData getMetaData() throws ResourceException
   {
      log.trace("getMetaData()");
      return new Snmp4JManagedConnectionMetaData();
   }

   /**
    * return the connection request info currently assigned to this managed connection
    * 
    * @return
    */
   public Snmp4JConnectionRequestInfo getRequestInfo() {
	   return requestInfo;
   }

   public SystemInfo systemInfo() throws ResourceException {
	   SystemInfo systemInfo = null;
	   
	   CommunityTarget target = new CommunityTarget();
	   
	   target.setCommunity(new OctetString(requestInfo.getCommunity()));
	   target.setAddress(new UdpAddress(requestInfo.getAddress(), requestInfo.getPort()));
	   target.setRetries(2);
	   target.setTimeout(1500);
	   target.setVersion(SnmpConstants.version2c);
	   
	   OID curOid = OID_sys;
	   systemInfo = new SystemInfo();
	   
	   while(curOid.startsWith(OID_sys)) {
		   try {
			   PDU pdu = new PDU();
			   
			   pdu.add(new VariableBinding(curOid));
			   			   
			   ResponseEvent event = snmp.getNext(pdu, target);
			   
			   if(event == null)
				   break;
			   
			   PDU response = event.getResponse();
			   
			   if(response == null)
				   break;

			   List<VariableBinding> bindings = response.getBindingList(curOid);

			   if(bindings.isEmpty())
				   break;
			   
			   for(VariableBinding vb : bindings) {

				   if(vb.getOid().equals(OID_sysDescr)) {
					   systemInfo.setDescription(vb.getVariable().toString());
				   }
				   
				   curOid = vb.getOid();
			   }
		   } catch (IOException e) {
			   log.warn("cannot execute SNMP request", e);
			   
			   throw new ResourceException(e);
		   }
		   
	   }
	   
	   return systemInfo;
   }
}
