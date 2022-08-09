package com.vmware.data.services.gemfire.operations.csv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vmware.data.services.gemfire.demo.ComplexObject;
import com.vmware.data.services.gemfire.demo.SimpleObject;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.*;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Organizer;

public class CsvRowConverterTest
{

	@Test
	public void testConvert()
	{
		CsvRowConverter c = new CsvRowConverter();
		
		assertNull(c.convert(null));
		
		assertEquals("\n",c.convert(""));

		assertEquals("1\n",c.convert(1));
		assertEquals("1.0\n",c.convert(1.0));
		
		
		//MM/dd/yyyy HH:mm:ss:SSSS
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.MARCH);
		cal.set(Calendar.DAY_OF_MONTH, 27);
		cal.set(Calendar.YEAR, 2013);
		cal.set(Calendar.HOUR_OF_DAY, 01);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);
		
		Date date = cal.getTime();
		assertEquals("03/27/2013 01:00:00:0000\n",c.convert(date));
		
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		assertEquals("03/27/2013 01:00:00:0000\n",c.convert(sqlDate));
		
		
		JavaBeanGeneratorCreator<ComplexObject> f = new JavaBeanGeneratorCreator<>(ComplexObject.class);
		//f.randomizeAll();
	
		ComplexObject complexObject = f.create();
		complexObject.setSimpleObject(new SimpleObject());
		complexObject.getSimpleObject().setFieldInt(23);
		
		String out = c.convert(complexObject);
		assertTrue(out.contains("23")&& out.contains("\n"));
		
		SimpleObject so = new JavaBeanGeneratorCreator<SimpleObject>
		(SimpleObject.class).randomizeAll().create();
		
		out = c.convert(so);
		System.out.println("out"+out);
 	}
	
	@Test
	public void testPdx() throws Exception
	{
		PdxInstance pdx = mock(PdxInstance.class);
		
		List<String> fieldNames = Organizer.toList("f1","f2","f3");
		
		when(pdx.getFieldNames()).thenReturn(fieldNames);
		when(pdx.getField(anyString())).thenReturn("hello");
		
		CsvRowConverter c = new CsvRowConverter();
		String out = c.convert(pdx);
		System.out.println(out);
		assertEquals("\"hello\",\"hello\",\"hello\"\n",out);
		
	}

}
