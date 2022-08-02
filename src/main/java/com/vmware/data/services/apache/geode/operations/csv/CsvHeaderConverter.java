package com.vmware.data.services.apache.geode.operations.csv;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import org.apache.geode.pdx.PdxInstance;

import nyla.solutions.core.io.csv.BeanPropertiesToCsvHeaderConverter;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.patterns.conversion.Converter;

public class CsvHeaderConverter implements Converter<Object, String>
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String convert(Object sourceObject)
	{
		if(sourceObject == null)
			return "\"\"\n";
		
		Class<?> cls = sourceObject.getClass();
				
		if(Date.class.isAssignableFrom(cls) || ClassPath.isPrimitive(cls))
			return new StringBuilder(cls.getSimpleName()).append("\n").toString();
		
		if(!PdxInstance.class.isAssignableFrom(cls))
		{
			if(beanPropertiesToCsvHeaderConverter == null)
				beanPropertiesToCsvHeaderConverter = new BeanPropertiesToCsvHeaderConverter();
			
			
			return beanPropertiesToCsvHeaderConverter.convert(sourceObject);
		}
		
		PdxInstance pdxInstance = (PdxInstance)sourceObject;
		Collection<String> fieldsList = pdxInstance.getFieldNames();
		

		if(fieldsList == null)
			return null;
		
		fieldsList = new TreeSet<String>(fieldsList);
		
		StringBuilder row = new StringBuilder();
		
		
		for (String fieldName : fieldsList)
		{	
			CsvWriter.addCell(row,fieldName);
		}
		row.append("\n");
		
		return row.toString();
	}

	private BeanPropertiesToCsvHeaderConverter<Object> beanPropertiesToCsvHeaderConverter = null;
}
