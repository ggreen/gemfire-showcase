package io.spring.gemfire.rest.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.io.QuerierMgr;
import com.vmware.data.services.gemfire.io.QuerierService;
import io.spring.gemfire.rest.app.exception.FaultAgent;
import nyla.solutions.core.security.user.data.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testing for REST service
 *
 * @author Gregory Green
 */
@ExtendWith(MockitoExtension.class)
public class GemFireQueryServiceControllerTest
{
    @Mock
    private UserProfile pi;

    @Mock
    private QuerierService querierService;

    @Mock
    private FaultAgent faultAgent;

    private ObjectMapper jsonObject = new ObjectMapper();

    private GemFireQueryServiceController subject;

    @BeforeEach
    void setUp() {
        subject = new GemFireQueryServiceController(querierService,
                faultAgent);
    }

    //@Test
    public void testQueryLimit()
    throws Exception
    {

        Collection<Object> expected = new ArrayList<>();
        expected.add(pi);

        when(querierService.query(anyString(), any())).thenReturn(expected);


        String query = null;
        int limit = 0;

        String json = subject.queryLimit(query, limit);

        assertNull(json);

        query = "select * from /data";

        json = subject.queryLimit(query, limit);
        assertNotNull(json);

        System.out.println("json:" + json);
        assertTrue(json.length() > 0);



        Object[] results = jsonObject.readValue(json, Object[].class);

        assertTrue(results != null && results.length > 0);


        //multiple results

        expected.add(pi);

        json = subject.queryLimit(query, limit);

        results = jsonObject.readValue(json, Object[].class);

        assertTrue(results != null && results.length > 1);

    }

    @Test
    public void testQuery_non_pdx()
    throws Exception
    {
        Collection<Object> expected = new ArrayList<>();
        String pi = "hello world";
        expected.add(pi);

        verifyQuery(expected);


    }

    @Test
    public void testQuery_string_with_specialchars()
    throws Exception
    {
        Collection<Object> expected = new ArrayList<>();
        String pi = "hello \" world~`!@#$%^&*()_-+= {[}]|\\:;'<,>.?/";
        expected.add(pi);

        verifyQuery(expected);
    }

    private void verifyQuery(Collection<Object> expected)
    throws Exception
    {
        when(this.querierService.query(anyString(), any())).thenReturn(expected);

        String query = "select * from /data";
        int limit = 0;

        String json = subject.queryLimit(query, limit);

        assertNotNull(json);

        System.out.println("json:" + json);
        assertTrue(json.length() > 0);


        Object[] results = jsonObject.readValue(json, Object[].class);

        assertTrue(results != null && results.length > 0);
    }

    @Test
    public void test_appendLimit()
    {
        String query = null;
        int limit = -1;

        assertNull(subject.appendLimit(query, limit));

        limit = -1;
        query = "";
        assertEquals(query, subject.appendLimit(query, limit));
        limit = 0;
        assertEquals(query, subject.appendLimit(query, limit));
        limit = 10;
        String results = subject.appendLimit(query, limit);
        assertNotEquals(query, results);

        assertEquals(results, " limit " + limit);

        query = "select * ";

        assertEquals(subject.appendLimit("select * from /region", limit),
                "select * from /region limit " + limit);

        assertEquals(subject.appendLimit("select * from /region where a = 1", limit),
                "select * from /region where a = 1 limit " + limit);
        assertEquals(subject.appendLimit("select * from /region where a = 1 group by a", limit),
                "select * from /region where a = 1 group by a limit " + limit);


        assertEquals(subject.appendLimit("select * from /region limit " + limit, limit),
                "select * from /region limit " + limit);

        assertEquals(subject.appendLimit("select * from /region where a = 1 limit " + limit, limit),
                "select * from /region where a = 1 limit " + limit);
        assertEquals(subject.appendLimit("select * from /region where a = 1 group by a limit " + limit, limit),
                "select * from /region where a = 1 group by a limit " + limit);


    }
}
