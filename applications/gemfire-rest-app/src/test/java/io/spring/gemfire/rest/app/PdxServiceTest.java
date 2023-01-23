package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.serialization.PDX;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PdxServiceTest
{
    private PDX pdx;
    private PdxService subject;

    @BeforeEach
    public void setUp() throws Exception
    {
        pdx = mock(PDX.class);
        subject = new PdxService(pdx);
    }

    @Test
    public void fromJSON()
    {
        String json = "{}";
        PdxInstance expected = mock(PdxInstance.class);
        when(pdx.fromJSON(anyString())).thenReturn(expected);
        PdxInstance actual = subject.fromJSON(json);
        assertEquals(expected,actual);
        assertNotNull(actual);


    }
    @Test
    public void toJSON()
    {
        String expected = "{}";
        PdxInstance pdxInstance = mock(PdxInstance.class);
        when(pdx.toJSON(any(),any())).thenReturn(expected);
        String type = Object.class.getName();
        String actual = subject.toJSON(pdxInstance, type);
        assertEquals(expected,actual);


    }
}