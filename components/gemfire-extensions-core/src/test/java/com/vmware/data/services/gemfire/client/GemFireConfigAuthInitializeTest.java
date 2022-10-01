package com.vmware.data.services.gemfire.client;

import nyla.solutions.core.io.IO;
import nyla.solutions.core.util.Config;
import org.apache.geode.security.AuthInitialize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * The VCAP configuration authorization initialization test
 *
 * @author Gregory Green
 */
public class GemFireConfigAuthInitializeTest
{
    @Test
    public void testSecurity_USERNAME_PASSWORD()
    {
        synchronized (GemFireConfigAuthInitializeTest.class)
        {

            System.setProperty("SECURITY_USERNAME", "myuser");
            System.setProperty("SECURITY_PASSWORD", "mypassword");

            Config.reLoad();

            GemFireConfigAuthInitialize auth = new GemFireConfigAuthInitialize(new GemFireSettings(""));

            Assertions.assertEquals("myuser", auth.getSecurityUserName());
            Assertions.assertEquals("mypassword", auth.getSecurityPassword());


            System.setProperty("security-username", "myuser2");
            System.setProperty("security-password", "mypassword2");

            Config.reLoad();

            auth = new GemFireConfigAuthInitialize(new GemFireSettings(""));

            Assertions.assertEquals("myuser2", auth.getSecurityUserName());
            Assertions.assertEquals("mypassword2", auth.getSecurityPassword());
        }

    }

    @Test
    public void testGetCredentials()
    throws Exception
    {
        synchronized (GemFireConfigAuthInitializeTest.class)
        {
            String vcap = IO.readClassPath("json/vcap.json");
            System.setProperty(GemFireSettings.VCAP_SERVICES, vcap);

            AuthInitialize auth = new GemFireConfigAuthInitialize(new GemFireSettings(vcap));

            Properties in = new Properties();

            Properties out = auth.getCredentials(in, null, false);

            System.out.println("output properties:" + out);


            String username = out.getProperty(GemFireConfigAuthInitialize.USER_NAME);
            Assertions.assertTrue(username != null && username.trim().length() > 0);
            Assertions.assertNotNull(out.getProperty(GemFireConfigAuthInitialize.PASSWORD));


        }

    }

}
