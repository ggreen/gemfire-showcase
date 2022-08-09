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
public class GeodeConfigAuthInitializeTest
{
    @Test
    public void testSecurity_USERNAME_PASSWORD()
    {
        synchronized (GeodeConfigAuthInitializeTest.class)
        {

            System.setProperty("SECURITY_USERNAME", "myuser");
            System.setProperty("SECURITY_PASSWORD", "mypassword");

            Config.reLoad();

            GeodeConfigAuthInitialize auth = new GeodeConfigAuthInitialize(new GeodeSettings(""));

            Assertions.assertEquals("myuser", auth.getSecurityUserName());
            Assertions.assertEquals("mypassword", auth.getSecurityPassword());


            System.setProperty("security-username", "myuser2");
            System.setProperty("security-password", "mypassword2");

            Config.reLoad();

            auth = new GeodeConfigAuthInitialize(new GeodeSettings(""));

            Assertions.assertEquals("myuser2", auth.getSecurityUserName());
            Assertions.assertEquals("mypassword2", auth.getSecurityPassword());
        }

    }

    @Test
    public void testGetCredentials()
    throws Exception
    {
        synchronized (GeodeConfigAuthInitializeTest.class)
        {
            String vcap = IO.readClassPath("json/vcap.json");
            System.setProperty(GeodeSettings.VCAP_SERVICES, vcap);

            AuthInitialize auth = new GeodeConfigAuthInitialize(new GeodeSettings(vcap));

            Properties in = new Properties();

            Properties out = auth.getCredentials(in, null, false);

            System.out.println("output properties:" + out);


            String username = out.getProperty(GeodeConfigAuthInitialize.USER_NAME);
            Assertions.assertTrue(username != null && username.trim().length() > 0);
            Assertions.assertNotNull(out.getProperty(GeodeConfigAuthInitialize.PASSWORD));


        }

    }

}
