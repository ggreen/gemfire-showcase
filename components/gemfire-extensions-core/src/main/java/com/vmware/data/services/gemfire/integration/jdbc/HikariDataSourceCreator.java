package com.vmware.data.services.gemfire.integration.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;

/**
 * HikariDataSourceCreator
 *
 * @author Gregory Green
 */
public class HikariDataSourceCreator implements Creator<DataSource>
{
    private final Logger logger;
    private HikariDataSource dataSource = null;

    public HikariDataSourceCreator()
    {
        this.logger = LogManager.getLogger(HikariDataSourceCreator.class);
    }

    @Override
    public DataSource create()
    {
        if(dataSource != null)
            return dataSource;

        String jdbcUrl = Config.getProperty("JDBC_URL");
        String driveClassName = Config.getProperty("JDBC_DRIVER_CLASS");

        try{


            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            Class.forName(driveClassName);
            config.setDriverClassName(driveClassName);
            config.setUsername( Config.getProperty("JDBC_USERNAME")  );
            config.setPassword( String.valueOf(Config.getPropertyPassword("JDBC_PASSWORD")));
            config.addDataSourceProperty( "cachePrepStmts" , Config.getPropertyBoolean("cachePrepStmts",true) );
            config.addDataSourceProperty( "prepStmtCacheSize" ,  Config.getProperty("prepStmtCacheSize","true") );//250
            config.addDataSourceProperty( "prepStmtCacheSqlLimit" ,  Config.getProperty("prepStmtCacheSqlLimit","2048") );
            this.dataSource = new HikariDataSource( config );

            return this.dataSource;
        }
        catch(ClassNotFoundException e)
        {
            logger.error("jdbcUrl:"+jdbcUrl+" driveClassName:"+driveClassName+ " STACK:"+Debugger.stackTrace(e));
            throw new RuntimeException("CLASS:"+driveClassName+" ERROR:"+e.getMessage(),e);
        }
        catch(Throwable e)
        {
            logger.error("jdbcUrl:"+jdbcUrl+" driveClassName:"+driveClassName+ " STACK:"+Debugger.stackTrace(e));
            throw e;
        }
    }
}
