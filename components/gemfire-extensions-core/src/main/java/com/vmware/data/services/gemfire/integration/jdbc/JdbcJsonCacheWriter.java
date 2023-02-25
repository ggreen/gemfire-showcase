package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public class JdbcJsonCacheWriter extends CacheWriterAdapter<String, PdxInstance> {
    private final Creator<DataSource> creator;
    private final Settings settings;

    private final Function<PdxInstance, String> converter;
    private Logger logger = LogManager.getLogger(JdbcJsonCacheWriter.class);

    public JdbcJsonCacheWriter()
    {
        this(new HikariDataSourceCreator(), pdx -> {return JSONFormatter.toJSON(pdx);}, Config.getSettings());
    }
    public JdbcJsonCacheWriter(Creator<DataSource> creator, Function<PdxInstance, String> converter, Settings settings) {
        this.creator = creator;
        this.settings = settings;
        this.converter = converter;
    }

    @Override
    public void beforeCreate(EntryEvent<String, PdxInstance> event) throws CacheWriterException {
        processEvent(event);
    }

    @Override
    public void beforeUpdate(EntryEvent<String, PdxInstance> event) throws CacheWriterException {
        processEvent(event);
    }

    private void processEvent(EntryEvent<String, PdxInstance> event) {
        String regionName = event.getRegion().getName();

        String upsertSQL = settings.getProperty(regionName.toUpperCase()+"_UPSERT_SQL");
        logger.info(upsertSQL);

        String json = converter.apply(event.getNewValue());

        DataSource dataSource = this.creator.create();

        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(upsertSQL))
            {
                preparedStatement.setString(1,json);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
