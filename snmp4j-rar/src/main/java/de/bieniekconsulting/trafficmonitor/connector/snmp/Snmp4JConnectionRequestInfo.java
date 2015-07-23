/**
 * 
 */
package de.bieniekconsulting.trafficmonitor.connector.snmp;

import java.net.InetAddress;

import javax.resource.spi.ConnectionRequestInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author rainer
 *
 */
class Snmp4JConnectionRequestInfo implements ConnectionRequestInfo {
	private InetAddress address;
	private int port;
	private String community;
	
	public Snmp4JConnectionRequestInfo(InetAddress address, int port, String community) {
		this.address = address;
		this.port = port;
		this.community = community;
	}

	/**
	 * @return the address
	 */
	InetAddress getAddress() {
		return address;
	}

	/**
	 * @return the port
	 */
	int getPort() {
		return port;
	}

	/**
	 * @return the community
	 */
	String getCommunity() {
		return community;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Snmp4JConnectionRequestInfo))
			return false;
		
		Snmp4JConnectionRequestInfo o = (Snmp4JConnectionRequestInfo)obj;
		
		return (new EqualsBuilder())
				.append(this.address, o.address)
				.append(this.port, o.port)
				.append(this.community, o.community)
				.isEquals();
	}
	
	public int hashCode() {
		return (new HashCodeBuilder())
				.append(address)
				.append(port)
				.append(community)
				.toHashCode();
	}
}
