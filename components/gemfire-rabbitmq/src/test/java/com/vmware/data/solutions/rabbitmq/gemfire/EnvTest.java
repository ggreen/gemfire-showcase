package com.vmware.data.solutions.rabbitmq.gemfire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EnvTest {

    @Test
    public void given_System_property_when_get_then_returned() {
        String key = "test";
        String expected = "hello";

        System.setProperty(key,expected);
        String actual = Env.getProperty(key);

        assertEquals(expected, actual);
    }

    @Test
    public void given_ENV_property_when_get_then_returned() {
        String key = "USER";
        String expected = System.getenv(key);

        String actual = Env.getProperty(key);

        assertEquals(expected, actual);
    }

    @Test
    public void given_integer_property_when_get_then_returned() {
        String key = "TIMES";
        Integer expected = 2;
        System.setProperty(key,expected.toString());

        Integer actual = Env.getPropertyInteger(key,0);

        assertEquals(expected, actual);
    }
}