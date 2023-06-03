package com.vmware.data.services.gemfire.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.demo.SimpleObject;
import nyla.solutions.core.security.user.data.UserProfile;

import java.io.Serializable;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.apache.geode.json.JsonDocument;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class SerializationJsonEntryWrapperTest
{

    private String key = "key";

    @Mock
    private JsonDocument pdxInstance ;
    private String expectJson = "{}";

    @Test
    public void isCustom()
    {
        assertTrue(SerializationJsonEntryWrapper.isCustom(new SimpleObject()));
        assertFalse(SerializationJsonEntryWrapper.isCustom(null));
        assertFalse(SerializationJsonEntryWrapper.isCustom(LocalDateTime.now()));
    }

    @Test
    public void test_setJson_not_allow_json_without_type()
            throws Exception
    {

        when(pdxInstance.toJson()).thenReturn(expectJson);

        var valueClassName = UserProfile.class.getName();
        var subject = new SerializationJsonEntryWrapper<String>(key, valueClassName, pdxInstance);

        assertEquals(key, subject.deserializeKey());
        assertEquals(key, subject.getKeyString());
        assertEquals(expectJson, subject.getValueJson());
        assertEquals(String.class.getName(), subject.getKeyClassName());
    }
}