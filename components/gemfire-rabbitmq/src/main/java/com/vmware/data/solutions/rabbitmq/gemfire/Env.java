package com.vmware.data.solutions.rabbitmq.gemfire;

/**
 * Alternative to Nyla Config
 * @author gregory green
 */
public class Env {
    /**
     *
     * @param property the name of the property
     * @return the value
     */
    public static String getProperty(String property) {
        return getProperty(property,null);
    }

    /**
     *
     * @param property the name of the property
     * @param defaultValue the default property value
     * @return the value or default
     */
    public static Integer getPropertyInteger(String property, Integer defaultValue) {
        return Integer.valueOf(getProperty(property,defaultValue.toString()));
    }

    public static String getProperty(String property, String defaultValue) {
        String value = System.getProperty(property);

        if(value != null)
            return value;

        value =  System.getenv(property);

        if(value != null)
            return value;

        if(defaultValue != null)
            return defaultValue;

        throw new IllegalArgumentException("ERROR: MISSING_PROPERTY property:"+property+" not found in system properties or environment variables");

    }
}
