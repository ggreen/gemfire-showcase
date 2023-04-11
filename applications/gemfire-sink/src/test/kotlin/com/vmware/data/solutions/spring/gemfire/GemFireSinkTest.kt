package com.vmware.data.solutions.spring.gemfire

import com.vmware.data.services.gemfire.serialization.PDX
import org.apache.geode.cache.Region
import org.apache.geode.pdx.PdxInstance
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class GemFireSinkTest{

    @Test
    internal fun write() {

        var pdx = mock(PDX::class.java);
        var expectedId = "hello";
        val keyFieldExpression = "id";
        val json = "{\"id\" : \"${expectedId}\"}";
        val expectedType = "java.lang.Object";

        val pdxInstance : PdxInstance = mock(PdxInstance::class.java);
        `when`(pdx.fromJSON(json)).thenReturn(pdxInstance);
        `when`(pdxInstance.getField(anyString())).thenReturn(expectedId);
        `when`(pdx.addTypeToJson(anyString(), anyString())).thenReturn(json);

        var region : Region<Any,PdxInstance> = mock(Region::class.java)  as Region<Any,PdxInstance>;

        var subject = GemFireSink(region,pdx,keyFieldExpression,expectedType);

        subject.accept(json);

        verify(pdx).addTypeToJson(json,expectedType);
        verify(pdx).fromJSON(json);

        verify(region).put(expectedId,pdxInstance);
    }
}