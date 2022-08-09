package com.vmware.data.services.gemfire.operations.functions;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.logging.log4j.Logger;



/**
 * <p>
 * 		The function will shutdown the distribute system 
 *      thus preventing disk stores from being corrupted.
 * </p>
 * 
 * <p>
 * 			gfsh&gt;execute function --group="gbc-data-node" --id="SystemShutdown"
 * </p>
 * 
 * <p>
 * 			Note the System.exit(0) will be executed to stop the JVM
 * </p>
 * 
 * @author Gregory Green
 *
 */
public class SystemShutDownFunction implements Function<Object>, Declarable
{	
	//@Autowired
	//private LoggingService loggingService;

	/**
	 * This method will the DistributeMember MBean and call to the shutdown method.
	 * Note that after the initiate function is executed other members may experience a 
	 * "Disconnected 
	 * 
	 */
	private static final long serialVersionUID = -4345180049555487810L;

	@Override
	public void execute(FunctionContext<Object> functionContext) 
	{
		
		String distributeMemberName = "unknown";
		Logger logger = null;
		
		try {
			
			Cache cache = CacheFactory.getAnyInstance();
			
			
			if(cache != null && !cache.isClosed())
			{
				DistributedSystem distributedSystem = cache.getDistributedSystem();
				
				//Assigned distributed member name
				distributeMemberName = distributedSystem.getDistributedMember().getName();
						
				if(distributedSystem.isConnected()  )
				{				
					MBeanServer jmx = ManagementFactory.getPlatformMBeanServer();
					
					ObjectName on = new ObjectName("GemFire:service=System,type=Distributed");
					
					
					logger = org.apache.logging.log4j.LogManager.getLogger(getClass());
					
					if(logger != null)
						logger.fatal("FUNCTION:SystemDownFunction invoking shutDownAllMembers on member:"+distributeMemberName);
				
					try
					{
						jmx.invoke(on, "shutDownAllMembers", null, null);
					}
					catch(Exception e)
					{
						String message = e.getMessage();
						
						if(message !=  null && message.contains("distributed system has been disconnected"))
						{
							//ignore and just exit JVM
							if(logger != null)
							{
								logger.warn("FUNCTION:SystemDownFunction shutting down disconnected member:"+distributeMemberName);
							}
							
							System.exit(0);
							
						}
						else
							throw e; //rethrow

						
					}
	
				}
			}
	
		} catch (Exception e) {
			if(logger != null) {
				logger.warn(e.toString());
			}
		}
		
		if(logger != null)
		{
			logger.warn("FUNCTION:SystemDownFunction shutting down member:"+distributeMemberName);
		}
		
		System.exit(0);

	}// --------------------------------------------

	/**
	 * @return Shutdown
	 */
	@Override
	public String getId() {
		return "SystemShutDownFunction";
	}

	@Override
	public boolean hasResult() {
		return false;
	}

	@Override
	public boolean isHA() {
		return false;
	}

	@Override
	public boolean optimizeForWrite() {
			return false;
	}

	public void init(Properties arg0)
	{
		// TODO Auto-generated method stub
		
	}
	

}
