package com.vmware.data.services.apache.geode.operations.csv;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import org.apache.geode.pdx.PdxInstance;


import nyla.solutions.core.io.csv.BeanPropertiesToCsvConverter;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Text;

/**
 * Converts PDX instance of java beans properties to CSV lines
 * 
 * @author Gregory Green
 *
 */
public class CsvRowConverter implements Converter<Object, String>
{

	/**
	 * DATE_FORMAT = Config.getProperty("DATE_FORMAT","MM/dd/yyyy HH:mm:ss:SSSS")
	 */
	public String DATE_FORMAT = Config.getProperty("DATE_FORMAT","MM/dd/yyyy HH:mm:ss:SSSS");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String convert(Object sourceObject)
	{
		if(sourceObject == null)
			return null;
		
		Class<?> cls = sourceObject.getClass();
		
		if(Date.class.isAssignableFrom(cls) )
			return new StringBuilder(Text.formatDate(DATE_FORMAT, (Date)sourceObject))
			.append("\n").toString();
		
		if(ClassPath.isPrimitive(cls))
			return new StringBuilder(sourceObject.toString())
							.append("\n").toString();
		
		
		if(!PdxInstance.class.isAssignableFrom(cls))
		{
			return new BeanPropertiesToCsvConverter(cls).convert(sourceObject);
		}
		
		PdxInstance pdxInstance = (PdxInstance)sourceObject;
		Collection<String> fieldsList = pdxInstance.getFieldNames();
		
		if(fieldsList == null)
			return null;
		
		fieldsList = new TreeSet<String>(fieldsList);
		
		StringBuilder row = new StringBuilder();
		
		Object field = null;
		for (String fieldName : fieldsList)
		{
			field = pdxInstance.getField(fieldName);
			
			CsvWriter.addCell(row,field ==  null? "" : field.toString());
		}
		
		row.append("\n");
		
		return row.toString();
	}

	//private BeanPropertiesToCsvConverter<Object> beanPropertiesToCsvConverter = null;
}
