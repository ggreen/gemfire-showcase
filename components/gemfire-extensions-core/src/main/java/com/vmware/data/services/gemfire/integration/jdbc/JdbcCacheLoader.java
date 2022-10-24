package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.patterns.jdbc.Sql;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import nyla.solutions.core.util.JavaBean;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * JdbcCacheLoader CacheLoader to load records from a relation database
 *
 * <pre>
 *
 *     Example Setup
 *
     deploy --jar=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/lib/HikariCP-4.0.3.jar
     deploy --jar=/Users/devtools/repositories/RDMS/PostgreSQL/driver/postgresql-42.2.9.jar
     deploy --jar=/Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1.jar
     deploy --jar=/Users/Projects/VMware/Tanzu/TanzuData/TanzuGemFire/dev/gemfire-extensions/components/gemfire-extenions-api/build/libs/gemfire-extensions-core-1.1.1-SNAPSHOT.jar
     ```

     java -DCRYPTION_KEY=PIVOTAL -classpath /Users/Projects/solutions/nyla/nyla-core/build/libs/nyla.solutions.core-1.5.1-SNAPSHOT.jar nyla.solutions.core.util.Cryption $1
     export JDBC_LOADER_TEST_CACHELOADER_SQL=select firstName as "firstName", lastName as "lastName", loginID as "loginID" from test_cacher where email = ?


    In gfsh
         create region --name=Test_CacheLoader  --type=PARTITION_PERSISTENT --cache-loader=com.vmware.data.services.gemfire.integration.jdbc.JdbcCacheLoader


     CREATE TABLE test_cacher (
     firstName varchar(255),
     lastName varchar(255),
     loginID varchar(255),
     email varchar(255)
     );

     insert into test_cacher(firstName, lastName, loginID,email)
     values
     (
     'Josiah',
     'Imani',
     'jimani',
     'jimani@test.unit'
     );


     get --key="jimani@test.unit" --region=/Test_CacheLoader


     query --query="select * from  /Test_CacheLoader"

     remove --region=/Test_CacheLoader --key="jimani@test.unit"
     </pre>
 * @author Gregory Green
 */
public class JdbcCacheLoader<K, V> implements CacheLoader<K, V>
{
    private final DataSource dataSource;
    private String sqlText = null;
    private Class<?> regionValueClass = null;
    private final Sql sql;
    private final Logger logger;

    public JdbcCacheLoader()
    {
        this(new HikariDataSourceCreator(),new Sql());
    }

    /**
     * Constructor for cache loader
     * @param dataSourceCreator creator for Data source
     * @param sql the SQL connection wrapper
     */
    public JdbcCacheLoader(Creator<DataSource> dataSourceCreator, Sql sql)
    {
        this.logger = LogManager.getLogger(JdbcCacheLoader.class);
        try
        {

            this.dataSource = dataSourceCreator.create();
            this.sql = sql;
        }
        catch(RuntimeException e)
        {
            logger.error(Debugger.stackTrace(e));
            throw e;
        }
    }

    /**
     * Load row from  table based on region key
     * @param helper the help class
     * @return the mapped row
     * @throws CacheLoaderException when an unknown exception occurs
     */
    @Override
    public V load(LoaderHelper helper) throws CacheLoaderException
    {
        Object key = null;
        String regionName = null;

        try {
             key = helper.getKey();

            if (sqlText == null) {
                regionName = helper.getRegion().getName().toUpperCase();
                this.sqlText = Config.getProperty("JDBC_CACHE_LOADER_" + regionName + "_SQL");
            }

            Object result;

            if (this.regionValueClass == null) {
                if (regionName == null)
                    regionName = helper.getRegion().getName().toUpperCase();

                this.regionValueClass = ClassPath.toClass(Config.getProperty("JDBC_LOADER_" + regionName + "_CLASS"));
            }

            try (Connection connection = dataSource.getConnection()) {
                Map<String, ?> map = sql.queryForMapWithFields(connection, sqlText, key);

                if (map == null)
                    return null;

                result = JavaBean.newBean(map, regionValueClass);

            }

            return (V) result;
        }
        catch (Exception e) {
            logger.error("Cannot load key:" + key + " ERROR:" + e.getMessage()+"  STACKTRACE:"+Debugger.stackTrace(e));
            return null;
        }
    }
}
