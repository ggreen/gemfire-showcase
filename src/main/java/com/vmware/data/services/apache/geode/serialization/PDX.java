package com.vmware.data.services.apache.geode.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.JSONFormatterException;
import org.apache.geode.pdx.PdxInstance;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author greg green
 * PDX conversion utility
 */
public class PDX
{
	/**
	 * JSON_TYPE_ATTRIBUTE = "@type"
	 */
	public static final String JSON_TYPE_ATTRIBUTE = "@type";

	public  String toJsonFromNonPdxObject(Object obj)
	{
		return new ToJsonFromNonPdxObject().convert(obj);
	}

	public  String toJSON(PdxInstance pdxInstance, String className)
	{

		String json = JSONFormatter.toJSON(pdxInstance);

		return addTypeToJson(json, className);

	}//-------------------------------------------
	public String addTypeToJson(String json, String type)
	{
		if(json ==null)
			return null;

		if(json.contains(JSON_TYPE_ATTRIBUTE))
			return json;

		if(json.trim().length() ==0)
			return null;

		StringBuilder prefix = new StringBuilder().append("{\"")
				.append(PDX.JSON_TYPE_ATTRIBUTE)
				.append("\":\"")
				.append(type).append("\"");

		if(json.indexOf(":") > 0 ||
				json.indexOf("[") > 0)
				prefix.append(",").toString();

		json = json.replaceFirst("\\{",prefix.toString());
		return json;
	}//-------------------------------------------
	public  PdxInstance fromJSON(String json)
	{
		try{



			 if(json == null || json.length() == 0)
			 	throw new IllegalArgumentException("json required");

			validateJson(json);

			return JSONFormatter.fromJSON(json);
		}
		catch(JSONFormatterException e){

			String message = e.getMessage();

			if(e.getCause() != null)
				message = message+" cause:"+e.getCause().getMessage();

			message += " json:"+json;

			throw new IllegalArgumentException(message);
		}
	}//-------------------------------------------

	public  void validateJson(String json)
	{
		if(!json.contains(JSON_TYPE_ATTRIBUTE))
			throw new IllegalArgumentException("Expected JSON to contain attribute:" + JSON_TYPE_ATTRIBUTE);

		try
		{
			new ObjectMapper().readTree(json);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Invalid json:"+json+" ERROR:"+e);
		}


	}

	public  SerializationPdxEntryWrapper toSerializePdxEntryWrapperFromJson(String json)
	{
		try
		{
			ObjectMapper om = new ObjectMapper();
			SerializationPdxEntryWrapper wrapper = om.readValue(json,SerializationPdxEntryWrapper.class);
			return wrapper;

		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}


	}//-------------------------------------------

	/**
	 *
	 * @param key the Region key
	 * @param pdxInstance the region value PDX
	 * @param <Key> the key type
	 * @param valueClassName the value class name
	 * @return the wrapper object
	 */
	public  <Key extends Serializable> SerializationPdxEntryWrapper toSerializePdxEntryWrapper(Key key, String valueClassName, PdxInstance pdxInstance)
	{
		return new SerializationPdxEntryWrapper(key,valueClassName,pdxInstance);
	}

	public  PdxInstance fromObject(Object obj)
	{
		if(obj instanceof PdxInstance)
			return (PdxInstance)obj;


		try
		{

			String json = toJsonFromNonPdxObject(obj);

			return JSONFormatter.fromJSON(json);

		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}//-------------------------------------------


}
