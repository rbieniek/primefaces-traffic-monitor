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
package de.bieniekconsulting.connector.snmp;

import org.jboss.logging.Logger;

/**
 * Snmp4JConnectionImpl
 *
 * @version $Revision: $
 */
public class Snmp4JConnectionImpl implements Snmp4JConnection
{
   /** The logger */
   private static Logger log = Logger.getLogger(Snmp4JConnectionImpl.class.getName());

   /** ManagedConnection */
   private Snmp4JManagedConnection mc;

   /** ManagedConnectionFactory */
   private Snmp4JManagedConnectionFactory mcf;

   /**
    * Default constructor
    * @param mc Snmp4JManagedConnection
    * @param mcf Snmp4JManagedConnectionFactory
    */
   public Snmp4JConnectionImpl(Snmp4JManagedConnection mc, Snmp4JManagedConnectionFactory mcf)
   {
      this.mc = mc;
      this.mcf = mcf;
   }

   /**
    * Call me
    */
   public void callMe()
   {
      mc.callMe();
   }

   /**
    * Close
    */
   public void close()
   {
      mc.closeHandle(this);
   }

}
