package com.vmware.data.services.gemfire.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.data.services.gemfire.demo.SimpleObject;
import nyla.solutions.core.security.user.data.UserProfile;

import java.io.Serializable;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;


public class SerializationPdxEntryWrapperTest
{

    @Test
    public void isCustom()
    {
        assertTrue(SerializationPdxEntryWrapper.isCustom(new SimpleObject()));
        assertFalse(SerializationPdxEntryWrapper.isCustom(null));
        assertFalse(SerializationPdxEntryWrapper.isCustom(LocalDateTime.now()));
    }

    @Test
    public void test_setJson_not_allow_json_without_type()
            throws Exception
    {
        SerializationPdxEntryWrapper<Serializable> s = new SerializationPdxEntryWrapper<>();

        try{
            s.setValueJson("");
        }
        catch(IllegalArgumentException e)
        {
            fail();
        }

        try{
            s.setValueJson("Sdfdsfsf sdfsfdsf");
            fail();
        }
        catch(IllegalArgumentException e)
        {

        }


        try{
            UserProfile expected = new UserProfile();
            expected.setEmail("test");

            s.setValueJson(new ObjectMapper().writeValueAsString(expected));
            fail();
        }
        catch(IllegalArgumentException e)
        {

        }

        try{
            s.setValueJson("@type");
            fail();
        }
        catch(IllegalArgumentException e)
        {
        }

        s.setValueJson("{\"@type\":\"java.util.Calendar\"}");

    }
}