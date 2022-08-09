package com.vmware.data.services.gemfire.util;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.ValueExp;

import com.vmware.data.services.gemfire.client.GemFireJmxClient;
import com.vmware.data.services.gemfire.client.SingletonGemFireJmx;
import org.apache.geode.management.DistributedSystemMXBean;
import org.apache.geode.management.MemberMXBean;

import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.util.Debugger;

/**
 * This object handles operations on the cluster 
 * such as shutting down members.
 * 
 * @author Gregory Green
 *
 */
public class GemFireMgmt
{
	/**
	 * Stops all cache servers followed by locators on a given server
	 * @param hostName the host name and or IP address
	 * @return the number of members
	 */
	public static int stopMembersOnHost(String hostName)
	{
		
		JMX jmx = SingletonGemFireJmx.getJmx();
		
		
		String objectNamePattern = "GemFire:type=Member,member=*";
		QueryExp queryExp = null;
		ValueExp[] values = null;
		
		
		//Also get the IP
		try
		{
			InetAddress[] addresses = InetAddress.getAllByName(hostName); 
			InetAddress address = null;
			if(addresses != null)
			{
				values = new ValueExp[addresses.length];
				
				for (int i=0; i <addresses.length;i++)
				{
					address = addresses[i];
					values[i] = Query.value(address.getHostAddress());
					
				}
			}
		}
		catch (UnknownHostException e)
		{
			Debugger.println(e.getMessage());
		}
		
		if(values != null)
		{
			queryExp = Query.or(Query.eq(Query.attr("Host"), Query.value(hostName)),
								Query.in(Query.attr("Host"), values));
		}
		else
		{
			queryExp = Query.eq(Query.attr("Host"), Query.value(hostName));
		}
		
		/*
		 * QueryExp query = Query.and(Query.eq(Query.attr("Enabled"), Query.value(true)),
               Query.eq(Query.attr("Owner"), Query.value("Duke")));
		 */
		Set<ObjectName> memberObjectNames =  jmx.searchObjectNames(objectNamePattern, queryExp);
		
		if(memberObjectNames == null || memberObjectNames.isEmpty())
			return 0;
		
		
		int memberCount = memberObjectNames.size();
		
		MemberMXBean member = null;
		
		Collection<String> locators = new ArrayList<String>();
		for (ObjectName objectName : memberObjectNames)
		{
			member = GemFireJmxClient.getMember(objectName.getKeyProperty("member"), SingletonGemFireJmx.getJmx());
			
			if(member.isLocator())
			{
				locators.add(member.getName());
			}
			else
			{
				shutDownMember(member.getName());
			}
			
		}
		
		for (String locatorName : locators)
		{			
			shutDownMember(locatorName);
		}
		
		return memberCount;
	}// --------------------------------------------------------
	/**
	 * Shut down a given member by its name
	 * @param name cache server name
	 */
	public static void shutDownMember(String name)
	{
		try
		{
			ObjectName serverName = new ObjectName("GemFire:type=Member,member="+name);
			
			JMX jmx = SingletonGemFireJmx.getJmx();

			MemberMXBean bean = jmx.newBean(MemberMXBean.class,serverName);
			
			bean.shutDownMember();
			
			//wait for member to shutdown
			System.out.println("Waiting for member:"+name+"  to shutdown");
			while(GemFireJmxClient.checkMemberStatus(name,SingletonGemFireJmx.getJmx()))
			{
				
				Thread.sleep(shutDownDelay);
			}
		}
		catch (MalformedObjectNameException e)
		{
			throw new RuntimeException("Unable to shutdown member "+name
					+" ERROR:"+e.getMessage(),e);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}// --------------------------------------------------------

	public static void stopLocator(JMX jmx,String locatorName)
	{
		try
		{
			ObjectName objectName  = new ObjectName("GemFire:type=Member,member="+locatorName);
			
			//DistributedSystemMXBean distributedSystemMXBean = 
			MemberMXBean bean = jmx.newBean(MemberMXBean.class, objectName);

			bean.shutDownMember();
		}
		catch (MalformedObjectNameException e)
		{
			throw new RuntimeException("Cannot stop member:"+locatorName+" ERROR:"+e.getMessage(),e);
		}

	}//------------------------------------------------

	//Does not stop locators
	public static String [] shutDown(JMX jmx)
	{
		try
		{
			
			DistributedSystemMXBean bean = toDistributeSystem(jmx);
			

			return bean.shutDownAllMembers();
			
		}
		catch (Exception e)
		{
			throw new RuntimeException(" ERROR:"+e.getMessage(),e);
		}
		
	}// --------------------------------------------------------
	private static DistributedSystemMXBean toDistributeSystem(JMX jmx) throws MalformedObjectNameException
	{
		ObjectName objectName  = new ObjectName("GemFire:service=System,type=Distributed");
		
		//DistributedSystemMXBean distributedSystemMXBean = 
		DistributedSystemMXBean bean = jmx.newBean(DistributedSystemMXBean.class, objectName);
		return bean;
	}//------------------------------------------------
	/**
	 * Dispose of the JMX/GemFire connection
	 */
	public static void disconnect()
	{
		SingletonGemFireJmx.dispose();
	}// --------------------------------------------------------
	/**
	 * Reconnect to a given locator
	 * @param locatorHost the locator host
	 * @param locatorPort the locator port
	 * @return the new JMX connect
	 */
   public static JMX reconnectJMX(String locatorHost, int locatorPort)
   {
	   return SingletonGemFireJmx.reconnectJMX(locatorHost, locatorPort);
   }// --------------------------------------------------------
   /**
    * Shut down each member in a given RedundancyZone
    * @param redundancyZone the Redundancy Zone to shutdown
    */
   public static void shutDownRedundancyZone(String redundancyZone)
   {
	   if (redundancyZone == null || redundancyZone.length() == 0)
		   throw new IllegalArgumentException("redundancyZone required");
	   
	   String objectNamePattern = "GemFire:type=Member,member=*";
	   QueryExp exp = Query.eq(Query.attr("RedundancyZone"),Query.value(redundancyZone));
	   
	   Collection<ObjectName> memberObjectNames = SingletonGemFireJmx.getJmx().searchObjectNames(objectNamePattern, exp);
	   for (ObjectName objectName : memberObjectNames)
	   {
		   GemFireMgmt.shutDownMember(objectName.getKeyProperty("member"));
	   }
   }// --------------------------------------------------------
   private static long shutDownDelay = 1000;
}
