package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class JdbcJsonPdxLoader implements CacheLoader<String,PdxInstance> {

    private final Creator<DataSource> dataSourceCreator;
    private final Function<String, PdxInstance> function;
    private final Settings settings;
    private Logger logger = LogManager.getLogger(JdbcJsonPdxLoader.class);

    public JdbcJsonPdxLoader()
    {
        this(new HikariDataSourceCreator(),
                json -> { return JSONFormatter.fromJSON(json);},
                Config.getSettings());
    }
    public JdbcJsonPdxLoader(Creator<DataSource> dataSourceCreator,  Function<String, PdxInstance> function, Settings settings) {
        this.dataSourceCreator = dataSourceCreator;
        this.function = function;
        this.settings = settings;
    }

    @Override
    public PdxInstance load(LoaderHelper<String,PdxInstance> helper) throws CacheLoaderException {

        DataSource dataSource = this.dataSourceCreator.create();

        try(Connection connection = dataSource.getConnection())
        {
            String sqlQuery = settings.getProperty(helper.getRegion().getName().toUpperCase()+"_SQL");
            logger.info("sqlQuery: {}",sqlQuery);

           try(PreparedStatement stmt = connection.prepareStatement(sqlQuery))
           {
               String key = helper.getKey();
               logger.info("Key: {}",key);

               stmt.setString(1,key);


               try(ResultSet rs = stmt.executeQuery())
               {
                   if(!rs.next())
                   {
                       logger.info("No data found for sql {} ",sqlQuery);
                       return null;
                   }

                   String json = rs.getString(1);
                   logger.info("SQL result JSON : {}"+json);
                   return function.apply(json);
               }
           }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
