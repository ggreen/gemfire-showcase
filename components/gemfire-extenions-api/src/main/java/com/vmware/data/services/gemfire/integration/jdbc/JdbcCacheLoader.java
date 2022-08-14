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
import java.sql.SQLException;
import java.util.Map;

/**
 * JdbcCacheLoader
 *
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
     * @throws CacheLoaderException
     */
    @Override
    public V load(LoaderHelper helper) throws CacheLoaderException
    {
        Object key = null;

        try {
             key = helper.getKey();

            String regionName = null;

            if (sqlText == null) {
                regionName = helper.getRegion().getName().toUpperCase();
                this.sqlText = Config.getProperty("JDBC_LOADER_" + regionName + "_SQL");
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
