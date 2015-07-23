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
import java.util.UUID;

import org.jboss.logging.Logger;

import javax.annotation.Resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bieniekconsulting.trafficmonitor.connector.snmp.Snmp4JConnection;
import de.bieniekconsulting.trafficmonitor.connector.snmp.Snmp4JConnectionFactory;
import static org.junit.Assert.*;

/**
 * ConnectorTestCase
 *
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class ConnectorTestCase
{
   private static Logger log = Logger.getLogger(ConnectorTestCase.class.getName());

   private static String deploymentName = "ConnectorTestCase";

   /**
    * Define the deployment
    *
    * @return The deployment archive
    */
   @Deployment
   public static ResourceAdapterArchive createDeployment()
   {
      ResourceAdapterArchive raa =
         ShrinkWrap.create(ResourceAdapterArchive.class, deploymentName + ".rar");
      JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
      ja.addPackages(true, Package.getPackage("de.bieniekconsulting.trafficmonitor.connector.snmp"));
      raa.addAsLibrary(ja);

      MavenResolverSystem resolver = Maven.resolver();
      
      raa.addAsLibraries(resolver
    		  .resolve("org.apache.commons:commons-lang3:3.3.2", "org.snmp4j:snmp4j:1.10.1")
    		  .withoutTransitivity()
    		  .as(JavaArchive.class));

      raa.addAsManifestResource("META-INF/ironjacamar.xml", "ironjacamar.xml");

      return raa;
   }

   /** Resource */
   @Resource(mappedName = "java:/eis/Snmp4JConnectionFactory")
   private Snmp4JConnectionFactory connectionFactory1;

   /**
    * Test getConnection
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testGetConnection1() throws Throwable
   {
      assertNotNull(connectionFactory1);
      Snmp4JConnection connection1 = connectionFactory1.getConnection(InetAddress.getLoopbackAddress(), "community");
      assertNotNull(connection1);
      connection1.close();
   }

   /**
    * Test getConnection
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testGetConnection2() throws Throwable
   {
      assertNotNull(connectionFactory1);
      Snmp4JConnection connection1 = connectionFactory1.getConnection(InetAddress.getLoopbackAddress(), 161, "community");
      assertNotNull(connection1);
      connection1.close();
   }

}
