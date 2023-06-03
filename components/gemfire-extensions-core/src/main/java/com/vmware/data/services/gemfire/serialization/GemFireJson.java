package com.vmware.data.services.gemfire.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.json.JsonDocument;
import org.apache.geode.json.JsonDocumentFactory;
import org.apache.geode.json.JsonParseException;
import org.apache.geode.json.StorageFormat;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author greg green
 * PDX conversion utility
 */
public class GemFireJson
{
	/**
	 * JSON_TYPE_ATTRIBUTE = "@type"
	 */
	public static final String JSON_TYPE_ATTRIBUTE = "@type";


	private final JsonDocumentFactory factory;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ToJsonFromNonPdxObject toJsonFromNonPdxConverter;

	public GemFireJson(JsonDocumentFactory factory) {
		this.factory = factory;
		this.toJsonFromNonPdxConverter = new ToJsonFromNonPdxObject();
	}

	public static GemFireJson createPdx() {
		return new GemFireJson(ClientCacheFactory.getAnyInstance().getJsonDocumentFactory(StorageFormat.PDX));
	}

	public  String toJsonFromNonPdxObject(Object obj)
	{
		return toJsonFromNonPdxConverter.convert(obj);
	}


	public String addTypeToJson(String json, String type)
	{
		if(json ==null)
			return null;

		if(json.contains(JSON_TYPE_ATTRIBUTE))
			return json;

		if(json.trim().length() ==0)
			return null;

		StringBuilder prefix = new StringBuilder().append("{\"")
				.append(GemFireJson.JSON_TYPE_ATTRIBUTE)
				.append("\":\"")
				.append(type).append("\"");

		if(json.indexOf(":") > 0 ||
				json.indexOf("[") > 0)
				prefix.append(",").toString();

		json = json.replaceFirst("\\{",prefix.toString());
		return json;
	}

	public JsonDocument fromJSON(String json)
	{
		try
		{
			 if(json == null || json.length() == 0)
			 	throw new IllegalArgumentException("json required");
			validateJson(json);

			return factory.create(json);
		}
		 catch (JsonParseException e) {
			String message = e.getMessage();

			if(e.getCause() != null)
				message = message+" cause:"+e.getCause().getMessage();

			message += " json:"+json;

			throw new IllegalArgumentException(message);
		}
	}

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

	public SerializationJsonEntryWrapper toSerializePdxEntryWrapperFromJson(String json)
	{
		try
		{
			ObjectMapper om = new ObjectMapper();
			SerializationJsonEntryWrapper wrapper = om.readValue(json, SerializationJsonEntryWrapper.class);
			return wrapper;

		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}


	}

	/**
	 *
	 * @param key the Region key
	 * @param pdxInstance the region value PDX
	 * @param <Key> the key type
	 * @param valueClassName the value class name
	 * @return the wrapper object
	 */
	public  <Key extends Serializable> SerializationJsonEntryWrapper toSerializePdxEntryWrapper(Key key, String valueClassName, JsonDocument pdxInstance)
	{
		return new SerializationJsonEntryWrapper(key,valueClassName,pdxInstance);
	}


}
