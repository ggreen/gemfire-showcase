package com.vmware.data.services.apache.geode.operations.functions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;


/**
 * <pre>
 * Import region data using exported JSON formatted data.
 * 
 * Example: 
 * gfsh&gt;execute function --id="ImportJsonFunction" --region=/securityRegion
 * 
 * </pre>
 * 
 * @see ExportJsonFunction
 * @author Gregory Green
 * 
 */
public class ImportJsonFunction implements Function<Object>
{
	private String directoryPath = "./";

	private static String fileSeparator = System.getProperty("file.separator");
	private static String suffix = ".json";

	/**
	 * String keyFileExtension = ".key"
	 */
	public static final String keyFileExtension = ".key";

	public ImportJsonFunction()
	{
	}// ------------------------------------------------

	public void execute(FunctionContext<Object> fc)
	{
		ResultSender<Object> rs = fc.getResultSender();

		try
		{
			boolean results = false;
			if (fc instanceof RegionFunctionContext)
			{
				results = importOnRegion((RegionFunctionContext) fc);
			} else
			{
				results = importAllRegions(fc);
			}

			rs.lastResult(results);
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

	private boolean importAllRegions(FunctionContext<Object> fc) throws Exception
	{

		String[] args = (String[]) fc.getArguments();

		if (args == null || args.length == 0) {
			throw new FunctionException("Argument not provided");
		}

		// Get region name from arguments
		String regionName = args[0];

		Cache cache = CacheFactory.getAnyInstance();

		Region<Object, Object> region = cache.getRegion(regionName);

		return importRegion(region);

	}// ------------------------------------------------

	private boolean importOnRegion(RegionFunctionContext rfc) throws Exception
	{
		// get argument

		// check if region is partitioned

		Region<Object, Object> region = rfc.getDataSet();

		return importRegion(region);
	}// ------------------------------------------------

	private boolean importRegion(Region<Object, Object> region)
			throws Exception
	{
		JsonNode node, keyNode, valueNode, keyClassName, valueClassName;

		Object key, value;

		if (PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}

		// get first

		ObjectMapper mapper = new ObjectMapper();

		// Configure to be very forgiving
		// mapper.configure(Feature.FAIL_ON_INVALID_SUBTYPE, false);
//		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.configure(Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
//		mapper.configure(Feature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
//
//		KeyDeserializers keyDeserializers = new KeyDeserializers()
//		{
//
//			@Override
//			public KeyDeserializer findKeyDeserializer(JavaType javatype,
//					DeserializationConfig deserializationconfig,
//					BeanDescription beandescription, BeanProperty beanproperty)
//					throws JsonMappingException
//			{
//				return new DefaultKeyDeserializer();
//			}
//		};
//
//		mapper.setDeserializerProvider(mapper.getDeserializerProvider()
//				.withAdditionalKeyDeserializers(keyDeserializers));

		// read JSON file
		String filePath = new StringBuilder(this.directoryPath)
				.append(fileSeparator).append(region.getName()).append(suffix)
				.toString();

		File file = new File(filePath);

		if (!file.exists())
		{
			LogManager.getLogger(getClass())
					.info(file.getAbsolutePath() + " does not exists");
			return false;
		}

		try(Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8))
		{

			// TokenBuffer buffer = new TokenBuffer
			JsonNode tree = mapper.readTree(reader);

			Iterator<JsonNode> children = tree.elements();

			if (children == null || !children.hasNext()) {
				return false;
			}

			while (children.hasNext())
			{
				node = children.next();
				keyNode = node.get("key");
				valueNode = node.get("value");
				keyClassName = node.get("keyClassName");
				valueClassName = node.get("valueClassName");

				key = mapper.readValue(keyNode.traverse(),
						forClassName(keyClassName));
				value = mapper.readValue(valueNode.traverse(),
						forClassName(valueClassName));

				region.put(key, value);
			}
			return true;
		}


	}// ------------------------------------------------
	/**
	 * <pre>
	 * Inner class that handles Jackson deserialization implementation of the Map key that may
	 * not be strings.
	 * 
	 * This methods expects the JSON version of key as the field name.
	 * 
	 * "{\"key\":{\"prop1\":\"123\",\"port2\":\"ABC\"},\"keyClassName\":\"exampe.MyObject\"}"
	 * </pre>
	 *
	 */
	static class DefaultKeyDeserializer extends KeyDeserializer
	{

		/**
		 * Add the JSON version of the key as the 
		 */
		@Override
		public Object deserializeKey(String keyString,
				DeserializationContext deserializationcontext)
				throws IOException, JsonProcessingException
		{

			if (keyString == null || keyString.length() == 0)
			{
				return null;
			}

			ObjectMapper objectMapper = new ObjectMapper();

			// JsonParser jp = deserializationcontext.getParser();

			JsonNode jsonNode = objectMapper.readTree(keyString);

			if (jsonNode.isTextual())
			{
				return keyString;
			}

			// jp.getCodec().readTree(jp);

			String jsonKey = jsonNode.get("key").toString();
			String keyClassName = jsonNode.get("keyClassName").asText();

			try
			{
				return objectMapper.readValue(jsonKey,
						Class.forName(keyClassName));
			}
			catch(UnrecognizedPropertyException e)
			{
				return null;
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException("Cannot create class name:"
						+ keyClassName, e);
			}
		}

	}

	protected Class<?> forClassName(JsonNode jsonNode)
			throws ClassNotFoundException
	{
		if (jsonNode == null) {
			throw new FunctionException("Class Name not found in json string");
		}

		String className = jsonNode.asText();

		if (className == null || className.length() == 0) {
			throw new FunctionException("class name json string is empty: "
					+ jsonNode.toString());
		}

		return Class.forName(className);
	}// --------------------------------------------------------

	/***
	 * @return ImportJsonFunction
	 */
	public String getId()
	{

		return "ImportJsonFunction";
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -3148806554381339703L;

}
