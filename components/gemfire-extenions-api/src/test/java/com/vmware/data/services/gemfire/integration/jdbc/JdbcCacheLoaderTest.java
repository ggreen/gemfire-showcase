package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.jdbc.Sql;
import nyla.solutions.core.security.user.data.UserProfile;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.JavaBean;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcCacheLoaderTest
{
    @Mock
    private Creator<DataSource> dataSourceCreator;
    @Mock
    private LoaderHelper helper;

    @Mock
    private Region<String,UserProfile> region;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Sql sql;

    private JdbcCacheLoader subject;

    @BeforeEach
    void setUp() throws SQLException
    {
        when(dataSourceCreator.create()).thenReturn(dataSource);

        subject = new JdbcCacheLoader(dataSourceCreator,sql);
    }

    @BeforeAll
    static void beforeAll()
    {
        System.setProperty("JDBC_LOADER_USERPROFILE_SQL","select * from UserProfile where id = ?");
        System.setProperty("JDBC_LOADER_USERPROFILE_CLASS",UserProfile.class.getName());
        Config.reLoad();
    }

    @Test
    void given_recordInDb_WHEN_load_THEN_record_loaded() throws SQLException
    {
        String regionName = "UserProfile";
        UserProfile expected = JavaBeanGeneratorCreator.of(UserProfile.class).create();
        Map map = JavaBean.toMap(expected);

        when(dataSource.getConnection()).thenReturn(connection);
        when(helper.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn(regionName);

        when(sql.queryForMapWithFields(any(Connection.class),anyString(),any())).thenReturn(map);


        Object actual = subject.load(helper);

        assertEquals(expected,actual);
    }

    @Test
    void given_Exception_when_load_THEN_return_null()
    {
        when(helper.getKey()).thenThrow(new IllegalArgumentException());

        assertDoesNotThrow(() -> subject.load(helper));
    }
}