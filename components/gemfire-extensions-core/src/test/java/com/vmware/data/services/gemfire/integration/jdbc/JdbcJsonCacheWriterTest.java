package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcJsonCacheWriterTest {

    @Mock
    private Creator<DataSource> creator;
    @Mock
    private DataSource dataSource;

    @Mock
    private PdxInstance pdxInstance;

    @Mock
    private Settings settings;

    @Mock
    private Function<PdxInstance,String> converter;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Region region;

    @Mock
    private EntryEvent<String, PdxInstance> event;

    @Test
    void given_event_when_beforeCreate_then_save_toDatabase() throws SQLException {
        String regionName = "region";

        when(creator.create()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(event.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn(regionName);

        String sql = "insert into ...";
        when(settings.getProperty(anyString())).thenReturn(sql);
        JdbcJsonCacheWriter subject = new JdbcJsonCacheWriter(creator,converter,settings);

        subject.beforeCreate(event);

        verify(preparedStatement).executeUpdate();
    }

    @Test
    void given_event_when_beforeUpdate_then_save_toDatabase() throws SQLException {
        String regionName = "region";

        when(creator.create()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(event.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn(regionName);

        String sql = "insert into ...";
        when(settings.getProperty(anyString())).thenReturn(sql);
        JdbcJsonCacheWriter subject = new JdbcJsonCacheWriter(creator,converter,settings);

        subject.beforeUpdate(event);

        verify(preparedStatement).executeUpdate();
    }
}