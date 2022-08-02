package com.vmware.data.services.apache.geode.qa.performance;

import org.apache.geode.cache.Region;

import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.JavaBean;

public   class PutRegionJavaBeanLoadRunner<T> implements Runnable
{

	public PutRegionJavaBeanLoadRunner(int count, Region<String, T> region, Class<T> clz, long sleepMs)
	{
		this.region = region;
		this.clz = clz;
		this.count = count;
		this.sleepMs  = sleepMs;
	}

	public void run()
	{
		try
		{
			JavaBeanGeneratorCreator<T> creator = new JavaBeanGeneratorCreator<>(clz);
			creator.randomizeAll();
			
			String regionName = region.getName();
			
			
			for (int i = 0; i < count; i++)
			{
				T created = creator.create();
				String id = JavaBean.getProperty(created, idPropertyName);
				region.put(id, created);	
				
				Thread.sleep(sleepMs);
			}
			System.out.println("PUT regionName:"+regionName+" count:"+count);
		}
		catch (InterruptedException e)
		{
			throw new SystemException(e.getMessage(),e);
		}
	}

	private final Class<T> clz;
	private final Region<String, T> region;
	private String idPropertyName = "id";
	private final int count;
	private final long sleepMs;
}
