package com.vmware.data.services.apache.geode.operations.functions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.vmware.data.services.apache.geode.serialization.SerializationMapKeyWrapper;
import com.vmware.data.services.apache.geode.serialization.SerializationRegionWrapper;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.logging.log4j.LogManager;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import nyla.solutions.core.util.Config;


/**
 * <pre>
 * 
 * Exports the JSON string of all region data on the server.
 * 
 * The output will be stored in the $gbc-cacheserver-{PERSISTENT_STORE_LOCATION} indicated directory.
 * One file will be written per region (format: ${region.name}.json)
 * 
 * Example:			gfsh&gt;execute function --id="ExportJsonFunction" --arguments=myRegion
 * </pre>
 * 
 * @author Gregory Green
 *
 */
public class ExportJsonFunction  implements Function<Object>
{
	private String directoryPath = Config.getProperty(this.getClass(),"directoryPath",".");
	
	private static String fileSeparator = System.getProperty("file.separator");
	private static String suffix = ".json";
	
	
	/**
	 * String keyFileExtension = ".key"
	 */
	public static final String keyFileExtension = ".key";
	
	public ExportJsonFunction()
	{
	}// ------------------------------------------------
	
	/**
	 * Export region data in JSON format
	 * @param fc the function context
	 */
	public void execute(FunctionContext<Object> fc)
	{
		
		ResultSender<Object> rs = fc.getResultSender();
		try
		{
			boolean didExport = false;
			
			if(fc instanceof RegionFunctionContext)
			{
				didExport = exportOnRegion((RegionFunctionContext)fc);	
			}
			else
			{
				didExport = exportAllRegions(fc);
			}
			
			rs.lastResult(didExport);
		}
		catch (Exception e)
		{
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			LogManager.getLogger(getClass()).error(sw.toString());
			rs.sendException(e);
		}
		
	    
	}// --------------------------------------------------------
	private boolean exportAllRegions(FunctionContext<Object> fc)
	{
		
		  String[] args = (String[]) fc.getArguments();
		    
		    if(args == null || args.length == 0)
		    {
		    	throw new FunctionException("Argument not provided");
		    }
		    
			
		//Get region name from arguments
		String regionName = args[0];
		
	       
		Cache cache = CacheFactory.getAnyInstance();
		
		Region<Object,Object> region = cache.getRegion(regionName);
		
		return exportRegion(region);
	
	}// ------------------------------------------------

	private boolean exportOnRegion(RegionFunctionContext rfc)
	{
		//get argument 
		
		//check if region is partitioned
	
		Region<Object,Object> region = rfc.getDataSet();
	    
		
	    return exportRegion(region);
	}// ------------------------------------------------

	private boolean  exportRegion(Region<Object, Object> region)
	{
		
		if(PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}
		
		//get first
	    
	    ObjectMapper  mapper = new ObjectMapper();
	    mapper.getSerializerProvider().setNullKeySerializer(new DefaultNullKeySerializer());
	    mapper.getSerializerProvider().setDefaultKeySerializer(new DefaultKeySerializer());

	    Set<Object> keySet = region.keySet();
	    
	    if(keySet == null || keySet.isEmpty())
	    {
	    	return false;	    	
	    }
	    
	    String regionName = region.getName();
	    
	    Collection<SerializationRegionWrapper> collection = new ArrayList<SerializationRegionWrapper>(keySet.size());
	    SerializationRegionWrapper serializationWrapper = null;
	    try
		{
	    	String keyClassName = null;
	    	Object value = null;
	    	String valueClassName = null;
	    	
			for (Object key : keySet)
			{
				keyClassName = key.getClass().getName();
				
				value = region.get(key);
				valueClassName = value.getClass().getName();
				
				serializationWrapper= new SerializationRegionWrapper(key,keyClassName,value, valueClassName);
				collection.add(serializationWrapper);
			}
			
			File resultFile = new File(new StringBuilder(this.directoryPath)
			.append(fileSeparator).append(regionName).append(suffix).toString());
			
			//write data
			mapper.writeValue(resultFile, collection);
			
			return true;
			
		}
		catch (RuntimeException e)
		{
			throw e;
		}
	    catch(Exception e)
	    {
	    	throw new FunctionException("Error exporting ERROR:"+ e.getMessage()+" serializationWrapper:"+serializationWrapper,e);
	    }
	}// ------------------------------------------------

	
	public String getId()
	{
		
		return "ExportJsonFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return false;
	}

	public boolean optimizeForWrite()
	{
		return true;
	}
	
	static class DefaultNullKeySerializer extends JsonSerializer<Object>
	{
	  @Override
	  public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) 
	      throws IOException, JsonProcessingException
	  {
	    jsonGenerator.writeFieldName("");
	  }
	}
	
	
	static class DefaultKeySerializer extends JsonSerializer<Object>
	{
	  @Override
	  public void serialize(Object key, JsonGenerator jsonGenerator, SerializerProvider unused) 
	      throws IOException, JsonProcessingException
	  {
		
		  if(key == null)
		  {
			  return;
			  
		  }

		  //jsonGenerator.writeFieldName(obj.getClass().getName());
		  
		  ObjectMapper objectMapper = new ObjectMapper();
		  
		   StringWriter stringWriter = new StringWriter();
		  
		  objectMapper.writeValue(stringWriter, new SerializationMapKeyWrapper(key));
	     jsonGenerator.writeFieldName(stringWriter.toString());
	     
	     
	  }
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3148806554381339703L;


	
}
