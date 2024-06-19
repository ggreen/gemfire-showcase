package com.vmware.data.services.gemfire.client;

import com.vmware.data.services.gemfire.io.QuerierMgr;
import com.vmware.data.services.gemfire.serialization.EnhancedReflectionSerializer;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.exception.RequiredException;
import nyla.solutions.core.exception.SetupException;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.PdxSerializer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * GemFireClientBuilder
 *
 * @author Gregory Green
 */
public class GemFireClientBuilder  implements Builder
{
    private String locators;
    private String clientName;
    private boolean useCachingProxy;
    private String pdxClassPatternProperty = ".*";
    private String pdxSerializerClassNm  = EnhancedReflectionSerializer.class.getName();
    private String logLevel = "config";
    private String sslKeystorePassword = "";
    private boolean poolPRSingleHopEnabled = true;
    private String sslKeystorePath = "";
    private String sslProtocols = "";
    private char[] sslTruststorePassword = "".toCharArray();
    private String sslKeystoreType = "";
    private String sslCiphers = "";
    private String sslRequireAuthentication = "";
    private String sslEnabledComponents = "";
    private String sslDirectory = ".";
    private static final Pattern regExpPattern = Pattern.compile("(.*)\\[(\\d*)\\].*");
    private final Settings settings = Config.settings();

    GemFireClientBuilder()
    {}

    @Override
    public Builder locators(String locators)
    {
        this.locators= locators;

        return this;
    }

    @Override
    public Builder clientName(String clientName)
    {
        this.clientName = clientName;
        return this;
    }

    @Override
    public Builder userName(String userName)
    {
        Properties prop = new Properties();
        prop.setProperty(GemFireConfigAuthInitialize.SECURITY_USER_PROP,userName);
        Config.setProperties(prop);

        return this;
    }

    @Override
    public Builder password(char[] password)
    {
        Properties prop = new Properties();
        prop.setProperty(GemFireConfigAuthInitialize.SECURITY_PASSWORD_PROP,String.valueOf(password));
        Config.setProperties(prop);

        return this;
    }

    @Override
    public GemFireClient build()
    {
        Iterable<URI> uris = getUris();

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

        String[] classPatterns = {pdxClassPatternProperty};

        Object [] initArgs = {classPatterns};
        PdxSerializer pdxSerializer = ClassPath.newInstance(pdxSerializerClassNm, initArgs);


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
               .setPoolPRSingleHopEnabled(poolPRSingleHopEnabled)
               .set("log-level", logLevel)
               .set("name", this.clientName);

        for (URI uri : uris)
        {
            factory.addPoolLocator(uri.getHost(), uri.getPort());
        }

        ClientCache clientCache = factory.create();

        //Caching Proxy
        ClientRegionFactory<Object, Object> cachingRegionfactory =
                clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);

        ClientRegionFactory<Object, Object> proxyRegionfactory =
                clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);

        QuerierMgr querier = new QuerierMgr();
        return new GemFireClient(clientCache,proxyRegionfactory,cachingRegionfactory,querier);
    }

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

        props.setProperty("ssl-keystore-password",sslKeystorePassword);

        props.setProperty("ssl-truststore",sslTrustStoreFilePath);
        props.setProperty("ssl-protocols", sslProtocols);
        props.setProperty("ssl-truststore-password",String.valueOf(sslTruststorePassword));
        props.setProperty("ssl-keystore-type",sslKeystoreType);
        props.setProperty("ssl-ciphers",sslCiphers);
        props.setProperty("ssl-require-authentication",sslRequireAuthentication);
        props.setProperty("ssl-enabled-components", sslEnabledComponents);

    }

    private File saveEnvFile(String configPropFilePath)
    throws IOException
    {

        String fileName = Paths.get(sslKeystorePath).toFile().getName();

        if(sslKeystorePath.length() == 0)
            return null;

        byte[] bytes = IO.readBinaryClassPath(sslKeystorePath);


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
    }

    String getLocators()
    {
        return locators;
    }

    String getClientName()
    {
        return clientName;
    }

    String getUserName()
    {
        return settings.getProperty(GemFireConfigAuthInitialize.SECURITY_USER_PROP,"");
    }

    char[] getPassword()
    {
        return  settings.getProperty(GemFireConfigAuthInitialize.SECURITY_PASSWORD_PROP,"").toCharArray();
    }

    public List<URI> getUris()
    {
        try {

            if(this.locators == null)
                throw new RequiredException("Locators is required ex: localhost[10334]");

            String[] parsedLocators = locators.split(",");
            ArrayList<URI> locatorList  = new ArrayList<>(parsedLocators.length);
            for (String locator : parsedLocators) {

                Matcher m = regExpPattern.matcher(locator);
                if (!m.matches())
                {
                    throw new IllegalStateException("Unexpected locator format. expected host[port], but got:" + locator);
                }

                locatorList.add(new URI("locator://" + m.group(1) + ":" + m.group(2)));
            }

            return locatorList;
        }
         catch (URISyntaxException e) {
            throw new SetupException("Cannot process locators "+locators+" ERROR:"+e.getMessage(),e);
         }
    }
}
