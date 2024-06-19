package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.patterns.jdbc.BindVariableInterpreter;
import nyla.solutions.core.patterns.jdbc.Sql;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * JdbcCacheWriter
 *
 * @author Gregory Green
 */
public class JdbcCacheWriter<K,V> extends CacheWriterAdapter<K,V>
{
    private final Creator<DataSource> dataSourceCreator;
    private final Sql sql;
    private final Logger logger;
    private final Settings settings = Config.settings();

    public JdbcCacheWriter()
    {
        this(new HikariDataSourceCreator(),new Sql());
    }
    public JdbcCacheWriter(Creator<DataSource> dataSourceCreator,
                           Sql sql)
    {
        this.logger = LogManager.getLogger(JdbcCacheWriter.class);
        this.dataSourceCreator = dataSourceCreator;
        this.sql = sql;
    }

    @Override
    public void beforeCreate(EntryEvent<K, V> event) throws CacheWriterException
    {
        processEvent(event);
    }

    @Override
    public void beforeUpdate(EntryEvent<K, V> event) throws CacheWriterException
    {
        processEvent(event);
    }

    /**
     * Write the event new value to the database
     * @param event the region event
     */
    private void processEvent(EntryEvent<K, V> event)
    {
        logger.info("Processing event {}",event);

        DataSource dataSource = dataSourceCreator.create();
        String regionName = event.getRegion().getName();

        try
        {
            try(Connection connection = dataSource.getConnection())
            {
                String jdbcSql = settings.getProperty("JDBC_CACHE_WRITER_"+regionName.toUpperCase()+"_SQL");

                logger.info("JDBC SQL {}",jdbcSql);

                BindVariableInterpreter upsertBindVariableInterpreter = new BindVariableInterpreter(jdbcSql);


                Object newValue = event.getNewValue();
                Object fieldValue;

                if(PdxInstance.class.isInstance(newValue))
                {
                    PdxInstance pdxInstance = (PdxInstance) newValue;
                    List<String> fieldNames = pdxInstance.getFieldNames();

                    logger.info("PDX fields {}",fieldNames);

                    try(PreparedStatement preparedStatement = sql.prepareStatement(connection,upsertBindVariableInterpreter))
                    {
                        for (String fieldName:fieldNames) {

                             fieldValue = pdxInstance.getField(fieldName);
                            upsertBindVariableInterpreter.setObject(preparedStatement,fieldName,fieldValue);
                            logger.info("set field {}={}",fieldName,fieldValue);

                        }

                        int result = preparedStatement.executeUpdate();

                        logger.info("Execute update count {}",result);
                    }
                }
                else
                    sql.executeUpdateWithJavaBean(connection,upsertBindVariableInterpreter,event.getNewValue());
            }
        }
        catch(SQLException e)
        {
            logger.error(Debugger.stackTrace(e));
           throw new RuntimeException(e);
        }
    }
}
