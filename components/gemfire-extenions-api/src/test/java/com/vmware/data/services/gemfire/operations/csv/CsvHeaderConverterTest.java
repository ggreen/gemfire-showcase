package com.vmware.data.services.gemfire.operations.csv;

import static org.junit.jupiter.api.Assertions.*;

import com.vmware.data.services.gemfire.demo.SimpleObject;
import org.junit.jupiter.api.*;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;

public class CsvHeaderConverterTest
{

	@Test
	public void testConvert()
	{
		CsvHeaderConverter c = new CsvHeaderConverter();
		
		assertEquals("\"\"\n",c.convert(null));
		assertEquals(String.class.getSimpleName()+"\n",c.convert(""));
		
		JavaBeanGeneratorCreator<SimpleObject> factory = new JavaBeanGeneratorCreator<>(SimpleObject.class);
		
		SimpleObject so = factory.create();
		
		String out = c.convert(so);
		
		System.out.println("out:"+out);
		
		assertNotNull(out);

	}

}
