package com.vmware.data.services.gemfire.client;

import nyla.solutions.core.util.Config;

public interface GeodeConfigConstants
{
	public final static String SSL_KEYSTORE_CLASSPATH_FILE_PROP = "SSL_KEYSTORE_CLASSPATH_FILE";
	
	public final static String PDX_SERIALIZER_CLASS_NM_PROP = "PDX_SERIALIZER_CLASS_NM";
	//public final static String PDX_SERIALIZER_CLASS_NM = Config.getProperty("PDX_SERIALIZER_CLASS_NM",ReflectionBasedAutoSerializer.class.getName()); 
	
	public final static String LOCATOR_HOST_PROP = "LOCATOR_HOST";
	public final static String LOCATOR_PORT_PROP = "LOCATOR_PORT";
	

	public final static String USER_NAME = "security-username";
	public final static String PASSWORD = "security-password";
	public final static String TOKEN = "security-token";
	
	/**
	 * PARTITION SINGLE HOP ENABLED (default false)
	 */
	public final static String POOL_PR_SINGLE_HOP_ENABLED_PROP = "POOL_PR_SINGLE_HOP_ENABLED";
	
	public final static String PDX_CLASS_PATTERN_PROP ="PDX_CLASS_PATTERN";
	
	//public final static String PDX_CLASS_PATTERN = Config.getProperty("PDX_CLASS_PATTERN",".*");
	
	public final static String USE_CACHING_PROXY_PROP = "USE_CACHING_PROXY";

	/**
	 * PDX_READ_SERIALIZED default false
	 */
	public final static  boolean PDX_READ_SERIALIZED = Config.getPropertyBoolean("PDX_READ_SERIALIZED",false);

	/**
	 * SSL_KEYSTORE_STORE_DIR_PROP = "SSL_KEYSTORE_STORE_DIR"
	 */
	public static final String SSL_KEYSTORE_STORE_DIR_PROP = "SSL_KEYSTORE_STORE_DIR";

	public static final String TRUSTED_KEYSTORE_FILE_NAME = "trusted.keystore";

	/**
	 * SSL_TRUSTSTORE_CLASSPATH_FILE_PROP = "SSL_TRUSTSTORE_CLASSPATH_FILE"
	 */
	public static final String SSL_TRUSTSTORE_CLASSPATH_FILE_PROP = "SSL_TRUSTSTORE_CLASSPATH_FILE";

	/**
	 * NAME_PROP = "NAME"
	 */
	public static final String NAME_PROP = "NAME";

	/**
	 * LOCATORS_PROP = "LOCATORS" use for the locator pool connections
	 */
	public static final String LOCATORS_PROP = "LOCATORS";
	 
}
