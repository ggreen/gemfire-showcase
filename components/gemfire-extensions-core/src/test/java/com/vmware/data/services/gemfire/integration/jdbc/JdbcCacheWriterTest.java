package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.jdbc.Sql;
import nyla.solutions.core.security.user.data.UserProfile;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Organizer;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for JdbcCacheWriter
 */
class JdbcCacheWriterTest
{
    private JdbcCacheWriter  subject;
    private DataSource dataSource;
    private EntryEvent event;
    private Sql sql;
    private Creator< DataSource > dataSourceCreator;
    private static String sqlWithBindVariables = "INSERT INTO customers (email, name) VALUES(:firstName,:email)";
    private Connection connection;
    private PreparedStatement preparedStatement;

    @BeforeAll
    static void beforeAll()
    {
        Properties properties = new Properties();
                properties.setProperty("JDBC_CACHE_WRITER_REGIONNAME_SQL",sqlWithBindVariables);

        Config.setProperties(properties);
    }

    @BeforeEach
    void setUp()
    {
        event = mock(EntryEvent.class);
        sql = mock(Sql.class);
        dataSourceCreator = mock(Creator.class);
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);


        subject = new JdbcCacheWriter(dataSourceCreator,sql);

    }

    @Test
    void given_entryDoesNotExistInDb_When_put_Then_insert_record() throws SQLException
    {
        Region<String, PdxInstance> region = mock(Region.class);

        when(dataSourceCreator.create()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(event.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn("regionName");

        UserProfile userProfile = JavaBeanGeneratorCreator.of(UserProfile.class).create();

        subject.beforeCreate(event);

        verify(sql).executeUpdateWithJavaBean(any(),any(),any());
    }



    @Test
    void given_pdxInstance_When_put_Then_setPreparedStatementWithPdxVales() throws SQLException
    {
        Region<String, PdxInstance> region = mock(Region.class);
        PdxInstance pdxInstance = mock(PdxInstance.class);

        List<String> fieldNames = Organizer.toList("name","email");
        when(pdxInstance.getFieldNames()).thenReturn(fieldNames);

        when(dataSourceCreator.create()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(sql.prepareStatement(any(),any())).thenReturn(preparedStatement);
        when(event.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn("regionName");
        when(event.getNewValue()).thenReturn(pdxInstance);
//        when(settings.getProperty(anyString())).thenReturn("insert into tables(name,email) values (:name,:email)");

        UserProfile userProfile = JavaBeanGeneratorCreator.of(UserProfile.class).create();

        subject.beforeCreate(event);

        verify(sql,never()).executeUpdateWithJavaBean(any(),any(),any());
        verify(pdxInstance).getFieldNames();
        verify(pdxInstance,atLeastOnce()).getField(any());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void given_entryDoesNotExistInDb_When_beforeUpdate_Then_insert_record() throws SQLException
    {
        Region<String, PdxInstance> region = mock(Region.class);

        when(dataSourceCreator.create()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        when(event.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn("regionName");

        UserProfile userProfile = JavaBeanGeneratorCreator.of(UserProfile.class).create();

        subject.beforeUpdate(event);

        verify(sql).executeUpdateWithJavaBean(any(),any(),any());
    }
}