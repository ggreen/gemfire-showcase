package io.spring.gemfire.rest.app.service;

import com.vmware.data.services.gemfire.serialization.GemFireJson;
import org.apache.geode.json.JsonDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PdxServiceTest
{
    private GemFireJson gemFireJson;
    private PdxService subject;

    @BeforeEach
    public void setUp() throws Exception
    {
        gemFireJson = mock(GemFireJson.class);
        subject = new PdxService(gemFireJson);
    }

    @Test
    public void fromJSON()
    {
        String json = "{}";
        JsonDocument expected = mock(JsonDocument.class);
        when(gemFireJson.fromJSON(anyString())).thenReturn(expected);
        JsonDocument actual = subject.fromJSON(json);
        assertEquals(expected,actual);
        assertNotNull(actual);


    }
//    @Test
//    public void toJSON()
//    {
//        String expected = "{}";
//        JsonDocument pdxInstance = mock(JsonDocument.class);
//        when(gemFireJson.toJsonFromNonPdxObject(any())).thenReturn(expected);
//        String type = Object.class.getName();
//        String actual = subject.toJSON(pdxInstance, type);
//        assertEquals(expected,actual);
//
//
//    }
}