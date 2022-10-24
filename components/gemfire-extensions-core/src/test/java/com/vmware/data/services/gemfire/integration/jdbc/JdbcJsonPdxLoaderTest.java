package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.util.settings.Settings;
import org.apache.geode.cache.LoaderHelper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcJsonPdxLoaderTest {

    @Mock
    private Creator<DataSource> creator;
    @Mock
    private DataSource dataSource;

    @Mock
    private LoaderHelper helper;

    @Mock
    private PdxInstance pdxInstance;

    @Mock
    private Settings settings;

    @Mock
    private Function<String, PdxInstance> converter;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Region region;

    @Test
    void given_results_when_load_returnPdxInstance() throws SQLException {

        String json = "{}";
        String regionName = "test";

        when(creator.create()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getString(anyInt())).thenReturn(json);
        when(resultSet.next()).thenReturn(true);
        when(converter.apply(anyString())).thenReturn(pdxInstance);
        when(helper.getRegion()).thenReturn(region);
        when(region.getName()).thenReturn(regionName);
        String sql = "select * from test where id = ?";
        when(settings.getProperty(anyString())).thenReturn(sql);

        JdbcJsonPdxLoader subject = new JdbcJsonPdxLoader(creator,converter,settings);

        PdxInstance actual = subject.load(helper);

        assertEquals(pdxInstance, actual);

    }
}