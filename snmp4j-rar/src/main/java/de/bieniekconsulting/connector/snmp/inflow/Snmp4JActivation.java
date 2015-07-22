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
package de.bieniekconsulting.connector.snmp.inflow;

import de.bieniekconsulting.connector.snmp.Snmp4JResourceAdapter;

import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;

/**
 * Snmp4JActivation
 *
 * @version $Revision: $
 */
public class Snmp4JActivation
{

   /** The resource adapter */
   private Snmp4JResourceAdapter ra;

   /** Activation spec */
   private Snmp4JActivationSpec spec;

   /** The message endpoint factory */
   private MessageEndpointFactory endpointFactory;

   /**
    * Default constructor
    * @exception ResourceException Thrown if an error occurs
    */
   public Snmp4JActivation() throws ResourceException
   {
      this(null, null, null);
   }

   /**
    * Constructor
    * @param ra Snmp4JResourceAdapter
    * @param endpointFactory MessageEndpointFactory
    * @param spec Snmp4JActivationSpec
    * @exception ResourceException Thrown if an error occurs
    */
   public Snmp4JActivation(Snmp4JResourceAdapter ra, 
      MessageEndpointFactory endpointFactory,
      Snmp4JActivationSpec spec) throws ResourceException

   {
      this.ra = ra;
      this.endpointFactory = endpointFactory;
      this.spec = spec;
   }

   /**
    * Get activation spec class
    * @return Activation spec
    */
   public Snmp4JActivationSpec getActivationSpec()
   {
      return spec;
   }

   /**
    * Get message endpoint factory
    * @return Message endpoint factory
    */
   public MessageEndpointFactory getMessageEndpointFactory()
   {
      return endpointFactory;
   }

   /**
    * Start the activation
    * @throws ResourceException Thrown if an error occurs
    */
   public void start() throws ResourceException
   {

   }

   /**
    * Stop the activation
    */
   public void stop()
   {

   }


}
