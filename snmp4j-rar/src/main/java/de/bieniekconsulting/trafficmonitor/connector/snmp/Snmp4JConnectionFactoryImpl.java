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

import java.net.InetAddress;

import org.jboss.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

/**
 * Snmp4JConnectionFactoryImpl
 *
 * @version $Revision: $
 */
public class Snmp4JConnectionFactoryImpl implements Snmp4JConnectionFactory
{

   /**
	 * 
	 */
	private static final long serialVersionUID = 6739041490388885457L;

	private static final int SNMP_PORT = 161;
	
/** The logger */
   private static Logger log = Logger.getLogger(Snmp4JConnectionFactoryImpl.class.getName());

   /** Reference */
   private Reference reference;

   /** ManagedConnectionFactory */
   private Snmp4JManagedConnectionFactory mcf;

   /** ConnectionManager */
   private ConnectionManager connectionManager;

   /**
    * Default constructor
    */
   public Snmp4JConnectionFactoryImpl()
   {

   }

   /**
    * Default constructor
    * @param mcf ManagedConnectionFactory
    * @param cxManager ConnectionManager
    */
   public Snmp4JConnectionFactoryImpl(Snmp4JManagedConnectionFactory mcf, ConnectionManager cxManager)
   {
      this.mcf = mcf;
      this.connectionManager = cxManager;
   }

   /** 
    * Get connection from factory
    *
    * @return Snmp4JConnection instance
    * @exception ResourceException Thrown if a connection can't be obtained
    */
   @Override
   public Snmp4JConnection getConnection(InetAddress address, String community) throws ResourceException
   {
	   return getConnection(address, SNMP_PORT, community);
   }

   
   /** 
    * Get connection from factory
    *
    * @return Snmp4JConnection instance
    * @exception ResourceException Thrown if a connection can't be obtained
    */
   @Override
   public Snmp4JConnection getConnection(InetAddress address, int port, String community) throws ResourceException
   {
      log.trace("getConnection()");
      return (Snmp4JConnection)connectionManager.allocateConnection(mcf, 
    		  new Snmp4JConnectionRequestInfo(address, port, community));
   }
   
   /**
    * Get the Reference instance.
    *
    * @return Reference instance
    * @exception NamingException Thrown if a reference can't be obtained
    */
   @Override
   public Reference getReference() throws NamingException
   {
      log.trace("getReference()");
      return reference;
   }

   /**
    * Set the Reference instance.
    *
    * @param reference A Reference instance
    */
   @Override
   public void setReference(Reference reference)
   {
      log.tracef("setReference(%s)", reference);
      this.reference = reference;
   }


}
