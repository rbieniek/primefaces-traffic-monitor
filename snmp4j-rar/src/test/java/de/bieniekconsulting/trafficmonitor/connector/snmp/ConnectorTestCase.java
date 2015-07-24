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

import static org.fest.assertions.Assertions.*;

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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.ext.StaticMOGroup;
import org.snmp4j.agent.mo.snmp.DateAndTimeScalar;
import org.snmp4j.agent.mo.snmp.DisplayStringScalar;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import de.bieniekconsulting.trafficmonitor.connector.snmp.Snmp4JConnection;
import de.bieniekconsulting.trafficmonitor.connector.snmp.Snmp4JConnectionFactory;
import de.bieniekconsulting.trafficmonitor.data.snmp.SystemInfo;

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

      ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
      ja.addPackages(true, SystemInfo.class.getPackage());
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

   private Snmp4jTestAgent testAgent;

   @BeforeClass
   public static void beforeClass() {
	   SNMP4JSettings.setThreadJoinTimeout(500);
   }
   
   @Before
   public void before() throws Exception {
	   testAgent = new Snmp4jTestAgent(61161);
/*
	   testAgent.registerManagedObject(new MOScalar<Integer32>(Snmp4JManagedConnection.OID_sysServices, 
			   MOAccessImpl.ACCESS_READ_ONLY, new Integer32(31)));
	   testAgent.registerManagedObject(new DisplayStringScalar<OctetString>(Snmp4JManagedConnection.OID_sysLocation, 
			   MOAccessImpl.ACCESS_READ_ONLY, new OctetString("test lab")));
	   testAgent.registerManagedObject(new DisplayStringScalar<OctetString>(Snmp4JManagedConnection.OID_sysName, 
			   MOAccessImpl.ACCESS_READ_ONLY, new OctetString("acme simulator")));
	   testAgent.registerManagedObject(new DisplayStringScalar<OctetString>(Snmp4JManagedConnection.OID_sysContact, 
			   MOAccessImpl.ACCESS_READ_ONLY, new OctetString("ACME, inc. admin")));
	   testAgent.registerManagedObject(new MOScalar<OID>(Snmp4JManagedConnection.OID_sysObjectID, 
			   MOAccessImpl.ACCESS_READ_ONLY, new OID(new int[] { 5, 3, 7, 1 })));
	   testAgent.registerManagedObject(new DisplayStringScalar<OctetString>(Snmp4JManagedConnection.OID_sysDescr, 
			   MOAccessImpl.ACCESS_READ_ONLY, new OctetString("simulator")));
*/
	   testAgent.registerManagedObject(new StaticMOGroup(Snmp4JManagedConnection.OID_sys, new VariableBinding[] {
			   new VariableBinding(Snmp4JManagedConnection.OID_sysDescr, new OctetString("simulator")),
			   new VariableBinding(Snmp4JManagedConnection.OID_sysObjectID, new OID(new int[] { 5, 3, 7, 1 })),
			   new VariableBinding(Snmp4JManagedConnection.OID_sysContact, new OctetString("ACME, inc. admin")),
			   new VariableBinding(Snmp4JManagedConnection.OID_sysName, new OctetString("acme simulator")),
			   new VariableBinding(Snmp4JManagedConnection.OID_sysLocation, new OctetString("test lab")),
			   new VariableBinding(Snmp4JManagedConnection.OID_sysServices, new Integer32(31)),
	   }));
	   
	   testAgent.start();
   }
   
   @After
   public void after() {
	   testAgent.stop();
   }
   
   /**
    * Test getConnection
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testGetConnection1() throws Throwable
   {
      assertThat(connectionFactory1).isNotNull();
      Snmp4JConnection connection1 = connectionFactory1.getConnection(InetAddress.getLoopbackAddress(), "community");
      assertThat(connection1).isNotNull();
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
	   assertThat(connectionFactory1).isNotNull();
      Snmp4JConnection connection1 = connectionFactory1.getConnection(InetAddress.getLoopbackAddress(), 161, "community");
      assertThat(connection1).isNotNull();
      connection1.close();
   }

   /**
    * Test getConnection
    *
    * @exception Throwable Thrown if case of an error
    */
   @Test
   public void testSystemInfo() throws Throwable
   {
      assertThat(connectionFactory1).isNotNull();
      Snmp4JConnection connection1 = connectionFactory1.getConnection(testAgent.getServerAddr().getAddress(), testAgent.getServerAddr().getPort(), "public");
      assertThat(connection1).isNotNull();
      
      SystemInfo sysInfo = connection1.systemInfo();
      
      assertThat(sysInfo).isNotNull();
      
      connection1.close();
   }

}
