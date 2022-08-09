package com.vmware.data.services.gemfire.qa.performance;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.geode.cache.Region;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.patterns.cache.Cache;
import nyla.solutions.core.patterns.cache.CacheFarm;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import nyla.solutions.core.patterns.workthread.MemorizedQueue;
import nyla.solutions.core.util.JavaBean;

public class BatchJavaBeanLoadRunner<T> implements Runnable
{

	private BatchJavaBeanLoadRunner(int start, int end, int batchSize, Region<String, T> region, Class<T> aClass)
	{
		super();
		this.start = start;
		this.end = end;
		this.batchSize = batchSize;
		this.region = region;
		this.aClass = aClass;
	}
	@Override
	public void run()
	{
		try
		{
			//Create prototype
			T ir = ClassPath.newInstance(aClass);
			JavaBeanGeneratorCreator<T> creator = new JavaBeanGeneratorCreator<>(ir);
			creator.randomizeAll();
			
			while(true)
			{
				Map<String,T> map = new Hashtable<>();
				String id =null;
				T created = null;
				for (int i = start; i < end; i++)
				{
					created = creator.create();

					id = JavaBean.getProperty(created, keyPropertyName);
					map.put(id, created);
					
					if(map.size() <  this.batchSize)
						continue;
					
					System.out.println("Putting key size:"+map.keySet().size());
					this.region.putAll(map);
					System.out.println("put records");
					map.clear();
					
				}
				
				if(!map.isEmpty())
					this.region.putAll(map);
				
				map.clear();
				
				try { Thread.sleep(100); } catch(Exception e) {}
				
			}
					
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}

	}
	
	 public static <T> long load(int count, int workerCount, int batchSize, Region<String,T> region, Class<T> clz)
	 throws Exception
	 {
		 Cache<Integer, ExecutorBoss> farm = CacheFarm.getCache();
		 
		ExecutorBoss boss = farm.get(workerCount);
		
		if(boss == null)
		{
			boss = new ExecutorBoss(workerCount);
			farm.put(workerCount, boss);
		}
		
		MemorizedQueue queue = new MemorizedQueue();
		int start, end, i;
		
		Set<String> keySet =region.keySetOnServer();
		if(keySet != null && keySet.size() > 0)
			count = count - keySet.size();
		
		int batchPageCnt = count/batchSize;
		for (i = 0; i < batchPageCnt; i++)
		{
			 start = (i*batchSize)+1;
			 end = start+(batchSize-1);
			
			queue.add(new BatchJavaBeanLoadRunner<>(start, end, batchSize, region,clz));
		}
		
		int remainder = count % batchSize;
		if(remainder > 0)
		{
			//remainder = count - (batchSize * i);
			start = (batchSize * i)+1;
			end = start + remainder;
			queue.add(new BatchJavaBeanLoadRunner<>(start, end, batchSize, region,clz));
		}
	
		int workQueueSize = queue.size();
		
		try
		{
			boss.startWorking(queue,true);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
		return workQueueSize;
	 }

	private final int start;
	private final int end;
	private final int batchSize;
	private final Region<String, T> region;
	private final Class<T> aClass;
	private final String keyPropertyName = "id";
	
	
}
