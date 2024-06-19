package com.vmware.data.services.gemfire.client;

import com.vmware.data.services.gemfire.client.cq.CqQueueListener;
import com.vmware.data.services.gemfire.io.GemFireIO;
import com.vmware.data.services.gemfire.io.QuerierMgr;
import com.vmware.data.services.gemfire.io.QuerierService;
import com.vmware.data.services.gemfire.io.function.FuncExe;
import com.vmware.data.services.gemfire.client.listeners.CacheListenerBridge;
import com.vmware.data.services.gemfire.lucene.GemFireLuceneSearch;
import com.vmware.data.services.gemfire.lucene.TextPageCriteria;
import com.vmware.data.services.gemfire.lucene.function.LuceneSearchFunction;
import com.vmware.data.services.gemfire.serialization.EnhancedReflectionSerializer;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.patterns.iteration.Paging;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.*;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;
import org.apache.geode.pdx.PdxSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;



/**
 *  GemFire (power by Apache Geode) API wrapper.
 *  
 *  export SSL_KEYSTORE_PASSWORD=pivotal
	export SSL_PROTOCOLS=TLSv1.2
	export SSL_TRUSTSTORE_PASSWORD=pivotal
	export SSL_KEYSTORE_TYPE=jks
	export SSL_CIPHERS=TLS_RSA_WITH_AES_128_GCM_SHA256
	export SSL_ENABLED_COMPONENTS=gateway,server,locator,jmx
	export SSL_REQUIRE_AUTHENTICATION=true
	export SSL_TRUSTSTORE_CLASSPATH_FILE=truststore.jks
	export SSL_KEYSTORE_CLASSPATH_FILE=keystore.jks
	export LOCATORS=hostname[ports]

 * @author Gregory Green
 *
 */
public class GemFireClient
{
	
	/**
	 * GemFire connection object is a Client Cache
	 */
	private final ClientCache clientCache;
	private boolean cachingProxy;
	private final ClientRegionFactory<?, ?> proxyRegionfactory;
	private final ClientRegionFactory<?, ?> cachingRegionfactory;
	private static GemFireClient gemFireClient = null;
	private Map<String,CacheListenerBridge<?, ?>> listenerMap = new Hashtable<>();
	private final QuerierService querier;

	/**
	 *
	 * @param clientCache the connection
	 */
	protected GemFireClient(ClientCache clientCache)
	{
		this(clientCache,
				clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY),
				clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU),
				new QuerierMgr()
		);
	}//------------------------------------------------
	protected GemFireClient(ClientCache clientCache, ClientRegionFactory<?, ?> proxyRegionfactory,
							ClientRegionFactory<?, ?> cachingProxyRegionfactory, QuerierService querier
	)
	{
		cachingProxy = false;
		this.clientCache = clientCache;
		this.proxyRegionfactory = proxyRegionfactory;
		this.cachingRegionfactory = cachingProxyRegionfactory;
		this.querier = querier;
	}//------------------------------------------------
	protected GemFireClient(boolean cachingProxy, String... classPatterns)
	{
		this.cachingProxy = cachingProxy;
			
		String name = Config.settings().getProperty(GemFireConfigConstants.NAME_PROP, GemFireClient.class.getSimpleName());
		
		//check for exists client cache
		ClientCache cache = null;
		
		try
		{
			cache = ClientCacheFactory.getAnyInstance();	
		}
		catch(CacheClosedException e)
		{
			Debugger.println(e.getMessage());
		}
		
		try{
			if(cache != null)
				cache.close(); //close old connection
		}catch(Exception e)
		{Debugger.println(e.getMessage());}
		
		String className = Config.settings().getProperty(
			GemFireConfigConstants.PDX_SERIALIZER_CLASS_NM_PROP,
			EnhancedReflectionSerializer.class.getName());
		
		PdxSerializer pdxSerializer = createPdxSerializer(className,classPatterns);
		
		Properties props = new Properties();
		try
		{
			constructSecurity(props);
		}
		catch(IOException e)
		{
			throw new ConfigException("Unable to configure security connection details ERROR:"+e.getMessage(),e);
		}
			
			ClientCacheFactory factory = new ClientCacheFactory(props);
			
		
			factory.setPoolSubscriptionEnabled(true)
			.setPdxSerializer(pdxSerializer)
			.setPdxReadSerialized(GemFireConfigConstants.PDX_READ_SERIALIZED)
			.setPoolPRSingleHopEnabled(Config.settings().getPropertyBoolean(
				GemFireConfigConstants.POOL_PR_SINGLE_HOP_ENABLED_PROP,true))
			.set("log-level", Config.settings().getProperty("log-level","config"))
			.set("name", name);
			
			//.addPoolLocator(host, port)
			GemFireSettings.getInstance().constructPoolLocator(factory);

			this.clientCache = factory.create();
			
			//Caching Proxy
			cachingRegionfactory = clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
			proxyRegionfactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);

			this.querier = new QuerierMgr();
		
	}//------------------------------------------------

	public static Builder builder()
	{
		return new GemFireClientBuilder();
	}

	/**
	 * Initialize security properties
	 * @param props the security properties
	 * @throws IOException when IOexception occurs
	 */
	protected void constructSecurity(Properties props) throws IOException
	{
		props.setProperty("security-client-auth-init", GemFireConfigAuthInitialize.class.getName()+".create");
		
		//write to file
		File sslFile = saveEnvFile(GemFireConfigConstants.SSL_KEYSTORE_CLASSPATH_FILE_PROP);
		
		System.out.println("sslFile:"+sslFile);

		File sslTrustStoreFile = saveEnvFile(GemFireConfigConstants.SSL_TRUSTSTORE_CLASSPATH_FILE_PROP);
		String sslTrustStoreFilePath = "";
		if(sslTrustStoreFile != null)
			sslTrustStoreFilePath = sslTrustStoreFile.getAbsolutePath();
		
		props.setProperty("ssl-keystore",(sslFile != null) ?  sslFile.getAbsolutePath(): "");

		props.setProperty("ssl-keystore-password",Config.settings().getProperty("ssl-keystore-password",""));
		
		props.setProperty("ssl-truststore",sslTrustStoreFilePath);
		props.setProperty("ssl-protocols",Config.settings().getProperty("ssl-protocols",""));
		props.setProperty("ssl-truststore-password",Config.settings().getProperty("ssl-truststore-password",""));
		props.setProperty("ssl-keystore-type",Config.settings().getProperty("ssl-keystore-type","")   );
		props.setProperty("ssl-ciphers",Config.settings().getProperty("ssl-ciphers",""));
		props.setProperty("ssl-require-authentication",Config.settings().getProperty("ssl-require-authentication","")  );
		props.setProperty("ssl-enabled-components", Config.settings().getProperty("ssl-enabled-components",""));
		
	}//------------------------------------------------

	/**
	 *
	 * @param configPropFilePath the property name with the file path
	 * @return the saved File
	 * @throws IOException when there is an issue with saving the file
	 */
	private static File saveEnvFile(String configPropFilePath)
	throws IOException
	{
		String sslKeystorePath = Config.settings().getProperty(configPropFilePath,"");

		String fileName = Paths.get(sslKeystorePath).toFile().getName();

		if(sslKeystorePath.length() == 0)
			return null;

		byte[] bytes = IO.readBinaryClassPath(sslKeystorePath);


		String sslDirectory = Config.settings().getProperty(GemFireConfigConstants.SSL_KEYSTORE_STORE_DIR_PROP,".");
		File sslDirectoryFile = Paths.get(sslDirectory).toFile();

		if(!sslDirectoryFile.exists())
		{
			throw new ConfigException("Configuration property "+ GemFireConfigConstants.SSL_KEYSTORE_STORE_DIR_PROP+" "+sslDirectoryFile+" but it does not exist");
		}
		else if(!sslDirectoryFile.isDirectory())
		{
			throw new ConfigException("Configuration property "+ GemFireConfigConstants.SSL_KEYSTORE_STORE_DIR_PROP+" "+sslDirectoryFile+" but is not a valid directory");
		}

		File sslFile = Paths.get(sslDirectoryFile+IO.fileSperator()+fileName).toFile();

		IO.writeFile(sslFile, bytes);

		return sslFile;
	}//------------------------------------------------

	public <ReturnType> Collection<ReturnType>  select(String oql)
	{
		return select(oql,null);
	}//------------------------------------------------
	PdxSerializer createPdxSerializer(String pdxSerializerClassNm, String... classPatterns )
	{
		
		Object [] initArgs = {classPatterns};
		return ClassPath.newInstance(pdxSerializerClassNm, initArgs);
	}

	//------------------------------------------------

	/**
	 * @param <K> the key class
	 * @param <V> the value class
	 * @param criteria the search criteria
	 * @param region the region
	 * @param filter the filter set
	 * @return collection of results
	 */
	public <K,V> Paging<V>  searchText(TextPageCriteria criteria, Region<K,V> region, Set<K> filter)
	{
		return searchText(criteria,region,filter,FuncExe.onRegion(region));
	}

	public <K,V> Paging<V>  searchText(TextPageCriteria criteria, Region<K,V> region, Set<K> filter, FuncExe funcExe)
	{
		try
		{
			LuceneSearchFunction<V> func = new LuceneSearchFunction();

			Paging<V> paging = (Paging)GemFireIO.exeWithResults(
					funcExe
							.withFilter(filter)
							.getExecution(), func);

			return paging;
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	public <ReturnType> Collection<ReturnType> select(String oql, RegionFunctionContext rfc)
	{
		return  querier.query(oql, rfc);
	}//------------------------------------------------


	/**
	 *
	 * @param criteria the search criteria
	 * @return the collection keys in the page region
	 * @throws Exception when an unknow exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> searchWithPageKeys(TextPageCriteria criteria)
			throws Exception
	{
		if(criteria == null)
			return null;

		return searchWithPageKeys(criteria,FuncExe.onRegion(getRegion(criteria.getRegionName())));
	}

	public Collection<String> searchWithPageKeys(TextPageCriteria criteria, FuncExe funcExe)
	throws Exception
	{
		if(criteria == null)
			return null;


		if(criteria.getFilter() != null)
		{
			funcExe.withFilter(criteria.getFilter());
		}

	    return funcExe.exe(new LuceneSearchFunction<Object>());

	}//------------------------------------------------

	public <K,V> Map<K,V> readSearchResultsByPage(TextPageCriteria criteria, int pageNumber)
	{
		GemFireLuceneSearch search = new GemFireLuceneSearch(this.clientCache);
		
		Region<String,Collection<?>> pageRegion = this.getRegion(criteria.getPageRegionName());
		Region<K,V> region = this.getRegion(criteria.getRegionName());
		
		return search.readResultsByPage(criteria,pageNumber,region,pageRegion);
	}//------------------------------------------------
	public Collection<String> clearSearchResultsByPage(TextPageCriteria criteria)
	{
		GemFireLuceneSearch search = new GemFireLuceneSearch(this.clientCache);
	
		return search.clearSearchResultsByPage(criteria,this.getRegion(criteria.getPageRegionName()));
		
	}//------------------------------------------------
	/**
	 * 
	 * @return the querier service instance
	 */
	public QuerierService getQuerierService()
	{
		return querier;
	}//------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <K,V> Region<K,V> createRegion(String regionName)
	{
		if(regionName.startsWith("/"))
			regionName = regionName.substring(1); //remove prefix
		
	
		
		CacheListenerBridge<K, V> listener = (CacheListenerBridge)this.listenerMap.get(regionName);
		
		if(listener != null)
		{
			ClientRegionFactory<K, V> listenerRegionFactory = null;
			
			if(this.cachingProxy)
				listenerRegionFactory = this.clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);	
			else
				listenerRegionFactory = this.clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
			
			listenerRegionFactory.addCacheListener(listener);
			
			Region<K,V> region = listenerRegionFactory.create(regionName);
			
			region.registerInterestRegex(".*");
			return region;
			
		}
		
		if(this.cachingProxy)
			return (Region<K,V>)this.cachingRegionfactory.create(regionName);
		else
			return (Region<K,V>)this.proxyRegionfactory.create(regionName);
		
		
	}//------------------------------------------------
	/**
	 * This is an example to get or create a region
	 * @param regionName the  name
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return the existing or created region (using the ClientbRegionFactory)
	 */
	@SuppressWarnings("unchecked")
	public <K,V> Region<K,V> getRegion(String regionName)
	{
		if(regionName == null || regionName.length() == 0)
			return null;
		
		Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
		
		if(region != null )
			return (Region<K,V>)region;
		
		region = this.createRegion(regionName);
		
		//Client side data policy is typically NORMAL or EMPTY
		if(cachingProxy)
		{
			//NORMAL data policy are typically used for CACHING_PROXY
			//You should interest so updates for the server will be pushed to the clients
			region.registerInterestRegex(".*");
		}
		
		return region;
	}//------------------------------------------------
	/**
	 * Create a proxy region
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param clientCache the client cache
	 * @param regionName the region name
	 * @return the create region
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Region<K,V> getRegion(ClientCache clientCache, String regionName)
	{
		if(regionName == null || regionName.length() == 0)
			return null;
		
		Region<K,V> region = clientCache.getRegion(regionName);
		
		if(region != null )
			return region;
		
		region = (Region<K,V>)clientCache
		.createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);
		
		return region;
	}//------------------------------------------------
	
	/**
	 * Create a proxy region
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param clientCache the client cache
	 * @param regionName the region name
	 * @param poolName the pool to use
	 * @return the create region
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Region<K,V> getRegion(ClientCache clientCache, String regionName, String poolName)
	{
		if(regionName == null || regionName.length() == 0)
			return null;
		
		Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
		
		if(region != null )
			return region;
		
		region = (Region<K,V>)clientCache
		.createClientRegionFactory(ClientRegionShortcut.PROXY).setPoolName(poolName).create(regionName);
		
		return region;
	}//------------------------------------------------
	public <T> BlockingQueue<T> registerCq(String cqName,String oql) 
	{
		try
		{
			QueryService queryService = this.clientCache.getQueryService();
			

			// Create CqAttribute using CqAttributeFactory
			CqAttributesFactory cqf = new CqAttributesFactory();

			// Create a listener and add it to the CQ attributes callback defined below
			CqQueueListener<T> cqListener = new CqQueueListener<T>();
			cqf.addCqListener(cqListener);
			CqAttributes cqa = cqf.create();
			// Name of the CQ and its query
			
			// Create the CqQuery
			CqQuery cqQuery = queryService.newCq(cqName, oql, cqa);
			
			cqListener.setCqQuery(cqQuery);

			// Execute CQ, getting the optional initial result set
			// Without the initial result set, the call is priceTracker.execute();
			cqQuery.execute();
			
			return cqListener;
		}
		catch (CqException| CqClosedException |RegionNotFoundException |QueryInvalidException | CqExistsException  e)
		{
		  throw new nyla.solutions.core.exception.SystemException
		  ("ERROR:"+e.getMessage()+" cqName:"+cqName+" oql:"+oql,e);
		}
	}
	//------------------------------------------------
	
	
	
	/**
	 * 
	 * @return the GEODE client
	 */
	public synchronized static GemFireClient connect()
	{
		if(gemFireClient != null)
			return gemFireClient;
		
		boolean cachingProxy = Config.settings().getPropertyBoolean(GemFireConfigConstants.USE_CACHING_PROXY_PROP,false);
		
		gemFireClient = new GemFireClient(cachingProxy,
		Config.settings().getProperty(GemFireConfigConstants.PDX_CLASS_PATTERN_PROP,".*"));
		
		return gemFireClient;
	}//------------------------------------------------
	
	/**
	 * @return the clientCache
	 */
	public ClientCache getClientCache()
	{
		return clientCache;
	}
	/**
	 * @return the cachingProxy
	 */
	public boolean isCachingProxy()
	{
		return cachingProxy;
	}
	/**
	 * @param cachingProxy the cachingProxy to set
	 */
	public void setCachingProxy(boolean cachingProxy)
	{
		this.cachingProxy = cachingProxy;
	}//------------------------------------------------
	/**
	 * Add the observer as a listener for put/create events
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param regionName the region name
	 * @param consumer the observer
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <K,V> void  registerAfterPut(String regionName, Consumer<EntryEvent<K, V>> consumer)
	{
		CacheListenerBridge<K, V> listener = (CacheListenerBridge)this.listenerMap.get(regionName);
		if(listener == null)
		{
			Region<K,V> region = clientCache.getRegion(regionName);
			
			if(region != null )
				throw new IllegalStateException("Cannot register a listener when the region already created. Try registering the listener first. Then use GeodeClient.getRegion for regionName:"+regionName);
			
			listener = CacheListenerBridge.forAfterPut(consumer);
		}
		else
		{
			listener.addAfterPutListener(consumer);
		}
		
		this.listenerMap.put(regionName, listener);
		
	}//------------------------------------------------
	/**
	 * Add the observer as a listener for remove/invalidate events
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param regionName the region name
	 * @param consumer the observer
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public   <K,V> void registerAfterDelete(String regionName, Consumer<EntryEvent<K,V>> consumer)
	{
		
		
		CacheListenerBridge<K, V> listener = (CacheListenerBridge)this.listenerMap.get(regionName);
		if(listener == null)
		{
			Region<K,V> region = clientCache.getRegion(regionName);
			
			if(region != null )
				throw new IllegalStateException("Cannot register a listener when the region already created. Try registering the listener first. Then use GeodeClient.getRegion for regionName:"+regionName);
			
			listener = CacheListenerBridge.forAfterDelete(consumer);
		}
		else
		{
			listener.addAfterDeleteListener(consumer);
		}
		
	}


	public <K,V> Region<K, V> getRegion(String regionName, CacheLoader<K, V> loader)
	{
		Region<K,V> region = this.getRegion(regionName);
		registerCacheLoader(region,loader);
		return region;
	}

	public static <K, V> void registerCacheLoader(Region<K, V> region,CacheLoader<K, V> loader)
	{
		region.getAttributesMutator().setCacheLoader(loader);
	}
}
