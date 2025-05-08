package io.spring.gemfire.perftest.components;

import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetFromPdxTest {
    @Test
    void apply()
    {
        var mockPdxInstance = mock(PdxInstance.class);
        String expected = "1";
        when(mockPdxInstance.getField("id")).thenReturn(expected);
        GetFromPdx subject = new GetFromPdx("id");

        String actual = subject.apply(mockPdxInstance);

        assertEquals(expected,actual);
    }
}