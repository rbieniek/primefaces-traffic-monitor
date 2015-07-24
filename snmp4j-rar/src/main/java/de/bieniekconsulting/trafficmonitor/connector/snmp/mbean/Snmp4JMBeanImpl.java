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
package de.bieniekconsulting.trafficmonitor.connector.snmp.mbean;

import java.net.InetAddress;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import de.bieniekconsulting.trafficmonitor.connector.snmp.Snmp4JConnection;
import de.bieniekconsulting.trafficmonitor.connector.snmp.Snmp4JConnectionFactory;
import de.bieniekconsulting.trafficmonitor.data.snmp.SystemInfo;

/**
 * Snmp4JMBeanImpl
 *
 * @version $Revision: $
 */
public class Snmp4JMBeanImpl implements Snmp4JMBean
{
   /** JNDI name */
   private static final String JNDI_NAME = "java:/eis/Snmp4J";

   /** MBeanServer instance */
   private MBeanServer mbeanServer;

   /** Object Name */
   private String objectName;

   /** The actual ObjectName instance */
   private ObjectName on;

   /** Registered */
   private boolean registered;


   /**
    * Set the MBean server
    * @param v The value
    */
   public void setMBeanServer(MBeanServer v)
   {
      mbeanServer = v;
   }

   /**
    * Start
    * @exception Throwable Thrown in case of an error
    */
   public void start() throws Throwable
   {
      if (mbeanServer == null)
         throw new IllegalArgumentException("MBeanServer is null");
      on = new ObjectName(mbeanServer.getDefaultDomain() + objectName);
      mbeanServer.registerMBean(this, on);
      registered = true;
   }

   /**
    * Stop
    * @exception Throwable Thrown in case of an error
    */
   public void stop() throws Throwable
   {
      if (registered)
         mbeanServer.unregisterMBean(on); 
   }

   /**
    * Call me
    * @throws Exception exception
    */
   @Override
   public SystemInfo systemInfo(InetAddress address, String community) throws Exception
   {
      return getConnection(address, community).systemInfo();
   }

   /**
    * Call me
    * @throws Exception exception
    */
   @Override
   public SystemInfo systemInfo(InetAddress address, int port, String community) throws Exception
   {
      return getConnection(address, port, community).systemInfo();
   }

   /**
    * GetConnection
    * @return Snmp4JConnection
    */
   private Snmp4JConnection getConnection(InetAddress address, String community) throws Exception
   {
      InitialContext context = new InitialContext();
      Snmp4JConnectionFactory factory = (Snmp4JConnectionFactory)context.lookup(JNDI_NAME);
      Snmp4JConnection conn = factory.getConnection(address, community);
      if (conn == null)
      {
         throw new RuntimeException("No connection");
      }
      return conn;
   }

   /**
    * GetConnection
    * @return Snmp4JConnection
    */
   private Snmp4JConnection getConnection(InetAddress address, int port, String community) throws Exception
   {
      InitialContext context = new InitialContext();
      Snmp4JConnectionFactory factory = (Snmp4JConnectionFactory)context.lookup(JNDI_NAME);
      Snmp4JConnection conn = factory.getConnection(address, port, community);
      if (conn == null)
      {
         throw new RuntimeException("No connection");
      }
      return conn;
   }

}
