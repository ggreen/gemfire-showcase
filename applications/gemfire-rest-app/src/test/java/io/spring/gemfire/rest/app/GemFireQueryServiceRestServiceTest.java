package io.spring.gemfire.rest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.io.QuerierMgr;
import com.vmware.data.services.gemfire.io.QuerierService;
import nyla.solutions.core.security.user.data.UserProfile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testing for REST service
 *
 * @author Gregory Green
 */

public class GemFireQueryServiceRestServiceTest
{
    private ObjectMapper jsonObject = new ObjectMapper();

    //@Test
    public void testQueryLimit()
    throws Exception
    {
        var qs = mock(QuerierService.class);

        Collection<Object> expected = new ArrayList<>();
        UserProfile pi = mock(UserProfile.class);
        expected.add(pi);

        when(qs.query(anyString(), any())).thenReturn(expected);

        GemFireQueryServiceRestService restService = new GemFireQueryServiceRestService();
        restService.querierService = qs;


        String query = null;
        int limit = 0;

        String json = restService.queryLimit(query, limit);

        assertNull(json);

        query = "select * from /data";

        json = restService.queryLimit(query, limit);
        assertNotNull(json);

        System.out.println("json:" + json);
        assertTrue(json.length() > 0);



        Object[] results = jsonObject.readValue(json, Object[].class);

        assertTrue(results != null && results.length > 0);


        //multiple results

        expected.add(pi);

        json = restService.queryLimit(query, limit);

        results = jsonObject.readValue(json, Object[].class);

        assertTrue(results != null && results.length > 1);

    }

    @Test
    public void testQuery_non_pdx()
    throws Exception
    {
        var qs = mock(QuerierMgr.class);

        Collection<Object> expected = new ArrayList<>();
        String pi = "hello world";
        expected.add(pi);

        verifyQuery(qs, expected);


    }

    @Test
    public void testQuery_string_with_specialchars()
    throws Exception
    {
        QuerierService qs = mock(QuerierService.class);

        Collection<Object> expected = new ArrayList<>();
        String pi = "hello \" world~`!@#$%^&*()_-+= {[}]|\\:;'<,>.?/";
        expected.add(pi);

        verifyQuery(qs, expected);
    }

    private void verifyQuery(QuerierService qs, Collection<Object> expected)
    throws Exception
    {
        when(qs.query(anyString(), any())).thenReturn(expected);

        GemFireQueryServiceRestService restService = new GemFireQueryServiceRestService();

        restService.querierService = qs;


        String query = "select * from /data";
        int limit = 0;

        String json = restService.queryLimit(query, limit);

        assertNotNull(json);

        System.out.println("json:" + json);
        assertTrue(json.length() > 0);


        Object[] results = jsonObject.readValue(json, Object[].class);

        assertTrue(results != null && results.length > 0);
    }

    //------------------------------------------------
    @Test
    public void test_appendLimit()
    {
        GemFireQueryServiceRestService restService = new GemFireQueryServiceRestService();
        String query = null;
        int limit = -1;

        assertNull(restService.appendLimit(query, limit));

        limit = -1;
        query = "";
        assertEquals(query, restService.appendLimit(query, limit));
        limit = 0;
        assertEquals(query, restService.appendLimit(query, limit));
        limit = 10;
        String results = restService.appendLimit(query, limit);
        assertNotEquals(query, results);

        assertEquals(results, " limit " + limit);

        query = "select * ";

        assertEquals(restService.appendLimit("select * from /region", limit),
                "select * from /region limit " + limit);

        assertEquals(restService.appendLimit("select * from /region where a = 1", limit),
                "select * from /region where a = 1 limit " + limit);
        assertEquals(restService.appendLimit("select * from /region where a = 1 group by a", limit),
                "select * from /region where a = 1 group by a limit " + limit);


        //--------------
        assertEquals(restService.appendLimit("select * from /region limit " + limit, limit),
                "select * from /region limit " + limit);

        assertEquals(restService.appendLimit("select * from /region where a = 1 limit " + limit, limit),
                "select * from /region where a = 1 limit " + limit);
        assertEquals(restService.appendLimit("select * from /region where a = 1 group by a limit " + limit, limit),
                "select * from /region where a = 1 group by a limit " + limit);


    }
}
