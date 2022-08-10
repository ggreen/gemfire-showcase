package com.vmware.data.services.gemfire.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.geode.cache.client.ClientCacheFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vmware.data.services.gemfire.security.UserSecuredCredentials;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.security.SecuredToken;
import nyla.solutions.core.util.Config;
import org.apache.geode.cache.client.PoolFactory;

/**
 * <pre>
 * VCAP is an environment variable used by cloudfoundry to pass configuration information
 *
 *{
  "p-cloudcache": [
    {
      "credentials": {
    "locators": [
      "10.244.0.4[55221]",
      "10.244.1.2[55221]",
      "10.244.0.130[55221]"
    ],
    "urls": {
      "gfsh": "http://cloudcache-5574c540-1464-4f9e-b56b-74d8387cb50d.local.pcfdev.io/gemfire/v1",
      "pulse": "http://cloudcache-5574c540-1464-4f9e-b56b-74d8387cb50d.local.pcfdev.io/pulse"
    },
    "users": [
      {
        "password": "some_developer_password",
        "username": "developer"
      },
      {
        "password": "some_password",
        "username": "cluster_operator"
      }
    ]
      },
      "label": "p-cloudcache",
      "name": "test-service",
      "plan": "caching-small",
      "provider": null,
      "syslog_drain_url": null,
      "tags": [],
      "volume_mounts": []
    }
  ]
 * </pre>
 * 
 * }
 */
public class GemFireSettings
{
	/**
	 * VCAP_SERVICES = "VCAP_SERVICES"
	 */
	public static final String VCAP_SERVICES = "VCAP_SERVICES";

	private static GemFireSettings instance = null;
	private final String envContent;
	private static final Pattern regExpPattern = Pattern.compile("(.*)\\[(\\d*)\\].*");

	private int port;

	private String host;

	/**
	 * Get from VCAP_SERVICES system environment variable or JVM system property
	 */
	private GemFireSettings()
	{

	this(System.getenv().get(VCAP_SERVICES) !=null 
		? System.getenv().get(VCAP_SERVICES) 
		: System.getProperty(VCAP_SERVICES));
		
	}// ------------------------------------------------

	GemFireSettings(String envContent)
	{
		this.envContent = envContent;
		
		List<URI> locatorList = getLocatorUrlList();
		URI locatorURI = null;
		
		if(locatorList != null && !locatorList.isEmpty())
				locatorURI = locatorList.get(0);
		
		port = 10334;
		
		host = Config.getProperty(GemFireConfigConstants.LOCATOR_HOST_PROP,"");
		
		if (host.trim().length() == 0)
		{
			//check env
			if(locatorURI != null)
			{
				host = locatorURI.getHost();
				port = locatorURI.getPort();
			}
		}
		else
		{
			port = Config.getPropertyInteger(GemFireConfigConstants.LOCATOR_PORT_PROP,10334).intValue();
			
			if(host.trim().length() == 0)
				throw new ConfigException(GemFireConfigConstants.LOCATOR_PORT_PROP+" configuration property required");
		}
		
	}// ------------------------------------------------

	/**
	 * 
	 * @return the VCAPConfig
	 */
	public static GemFireSettings getInstance()
	{
		synchronized (GemFireSettings.class)
		{
			if (instance == null)
			{
				instance = new GemFireSettings();
			}
		}
		return instance;
	}// ------------------------------------------------

	/**
	 * Build the pool locator connection details
	 * @param locators the locator connection string
	 * @param factory the pool factory
	 */
	public static void constructLocators(String locators, PoolFactory factory)
	{
		constructConnection(locators,new PoolFactoryBuilder(factory));

	}//------------------------------------------------

	private static void constructConnection(String locators,ConnectionBuilder builder)
	{
		if(locators == null || locators.length() == 0)
			throw new IllegalArgumentException("locators is required");

		String[] parsedLocators = locators.split(",");

		String host,portText;
		int port;
		for (String hostPort : parsedLocators)
		{
			Matcher m = regExpPattern.matcher(hostPort);
			if (!m.matches())
			{
				throw new IllegalStateException("Unexpected locator format. expected host[port], but got:" +  hostPort);
			}

			host = m.group(1);
			portText = m.group(2);

			try{
				port = Integer.parseInt(portText);
			}
			catch(NumberFormatException e){
				throw new IllegalStateException("Invalid port expected host[port], but got:" +  hostPort);
			}

			builder.addHostPort(host,port );
		}
	}//------------------------------------------------

	@SuppressWarnings("unchecked")
	public String getLocators()
	{
		StringBuilder locatorList = new StringBuilder();
		Map<String, ?> credentials = getCredentials();

		if (credentials == null)
			return null;

		List<String> locators = (List<String>) credentials.get("locators");
		for (String locator : locators)
		{

			if (locatorList.length() != 0)
				locatorList.append(",");

			locatorList.append(locator);
		}
		return locatorList.toString();
	}// ------------------------------------------------

	@SuppressWarnings("unchecked")
	public List<URI> getLocatorUrlList() 

	{
		List<URI> locatorList = new ArrayList<URI>();
		Map<String, ?> credentials = getCredentials();
		List<String> locators = null;
		
		if (credentials != null)
			locators = (List<String>) credentials.get("locators");
			
		try
		{
			if(locators == null || locators.isEmpty())
			{
				//get for LOCATORS env
				String locatorsConfig = Config.getProperty(GemFireConfigConstants.LOCATORS_PROP,"");
				if(locatorsConfig.length() == 0)
					return null;
	
				String[] parsedLocators = locatorsConfig.split(",");
				
				if(parsedLocators == null || parsedLocators.length == 0)
					return null;
				
				locators = Arrays.asList(parsedLocators);
			}
			
			for (String locator : locators)
			{
				Matcher m = regExpPattern.matcher(locator);
				if (!m.matches())
				{
					throw new IllegalStateException("Unexpected locator format. expected host[port], but got:" + locator);
				}
				locatorList.add(new URI("locator://" + m.group(1) + ":" + m.group(2)));
			}
			return locatorList;
		
		}
		catch (URISyntaxException e)
		{
			throw new ConfigException("One of the provided locators has an incorrect syntax:"+locatorList);
		}
		

	}// ------------------------------------------------

	
	/**
	 * 
	 * @return the secured token information
	 */
	public SecuredToken getSecuredToken()
	{
		return getSecuredToken(null, null);
	}// ------------------------------------------------

	/**
	 * 
	 * @param token
	 *            the token to initialize
	 * @return the secured token information
	 */
	public SecuredToken getSecuredToken(String token)
	{
		return getSecuredToken(null, token);
	}// ------------------------------------------------

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public SecuredToken getSecuredToken(String username, String token)
	{

		Map<String, ?> credentials = getCredentials();

		if (credentials == null)
			return null;

		List<Map<String, String>> users = (List) credentials.get("users");

		if (users == null)
			return null;

		Map<String, String> map = null;

		if (username == null || username.trim().length() == 0)
		{
			map = users.iterator().next();
		}
		else
		{
			map = users.stream()
			.filter(m -> username.equals(m.get("username")))
			.findFirst().orElse(null);
		}

		if (map == null)
			return null;

		String password = map.get("password");
		if (password == null)
			password = "";

		return new UserSecuredCredentials(map.get("username"), password.toCharArray(), token);
	}// ------------------------------------------------

	/**
	 * 
	 * @return the map with credentials
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Map<String, ?> getCredentials()
	{

		if (envContent == null || envContent.trim().length() == 0)
			return null;

		Map<String, ?> credentials = null;

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, ?> services = objectMapper.readValue(envContent, Map.class);
			List<Map<String, ?>> gemfireService = getGemFireService(services);
			if (gemfireService != null)
			{
				Map<String, ?> serviceInstance = (Map) gemfireService.get(0);
				credentials = (Map<String, ?>) serviceInstance.get("credentials");
			}

			return credentials;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}// ------------------------------------------------

	public void constructPoolLocator(ClientCacheFactory factory)
	{
		
		List<URI> list = this.getLocatorUrlList();
		if(list != null && !list.isEmpty())
		{
			for (URI uri : list)
			{
				factory.addPoolLocator(uri.getHost(), uri.getPort());
			}
		}
		else
		{
			factory.addPoolLocator(this.host, this.port);
		}
	}//------------------------------------------------
	
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private List<Map<String, ?>> getGemFireService(Map services)
	{
		List<Map<String, ?>> l = (List) services.get("p-cloudcache");
		return l;
	}

	String getLocatorHost()
	{
		return this.host;
	}


}
