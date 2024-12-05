package showcase.gemfire.security;

import nyla.solutions.core.data.MapEntry;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.settings.ConfigSettings;
import nyla.solutions.core.util.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SettingsUserServiceTest
{
    private Properties properties;
    private Cryption cryption;
    private ConfigSettings settings;
    private Settings mockSettings;
    private  File file;

    @BeforeEach
    void setUp() throws Exception
    {
        mockSettings = mock(Settings.class);
        properties = new Properties();
        cryption = new Cryption();
        properties.setProperty("gemfire.security-users.nyla", "{cryption}" + cryption.encryptText("nyla"));
        properties.setProperty("gemfire.security-users.josiah ", "{cryption}" + cryption.encryptText("josiah"));
        properties.setProperty("gemfire.security-users.cassandra ", "{cryption}" + cryption.encryptText("cassandra")+" ");
        properties.setProperty("CRYPTION_KEY", "PIVOTAL");
        properties.setProperty("CONFIG_FILE_WATCH_POLLING_INTERVAL_MS", "1000");

        file = Paths.get("src/test/resources/junit_users.properties").toFile();
        properties.store(new FileWriter(file), null);

        System.setProperty(Config.SYS_PROPERTY, file.getAbsolutePath());
        settings = new ConfigSettings();

    }

    @Test
    void given_userWithSpaceInProperty_whenFindUser_ThenNoSpaceExists()
    {
        String expectedUserName = "josiah";
        SettingsUserService subject = new SettingsUserService(settings);
        User actual = subject.findUser(expectedUserName);
        assertNotNull(actual);
        assertEquals(expectedUserName,actual.getUserName());
    }

    @Nested
    class CreateUser
    {
        private SettingsUserService subject;
        private Settings mockSettings;
        @BeforeEach
        void setUp()
        {
            mockSettings = mock(Settings.class);
            subject = new SettingsUserService(mockSettings);
        }

        @Test
        void createUserFromEntry_whenUSerHasSpace_ThenFindTrimmed() throws Exception
        {
            String expectedUserName = "josiah";
            String propertyName ="gemfire.security-users."+expectedUserName+" ";
            String password = "{cryption}" + cryption.encryptText("josiah");
            Map.Entry<Object, Object> e = new MapEntry<>(propertyName,password);
            User actual = subject.createUserFromEntry(e);
            assertEquals(expectedUserName,actual.getUserName());
        }

        @Test
        void createUserFromEntry_whenPropertyEmpty_ThenThrowsSetupException() throws Exception
        {
            String expectedUserName = "josiah";
            String propertyName ="gemfire.security-users."+expectedUserName+" ";
            String value = null;
            Map.Entry<Object, Object> e = new MapEntry<>(propertyName,value);
            assertThrows(IllegalArgumentException.class, () -> subject.createUserFromEntry(e));
        }

        @Test
        void createUserFromEntry_whenHasPriviledges_ThenSetValues() throws Exception
        {
            String expectedUserName = "josiah";
            String propertyName ="gemfire.security-users."+expectedUserName+" ";
            String priviledge1= "ADMIN";
            String priviledge2 = "READ";
            String privledges = priviledge1+","+priviledge2;
            String value = "{cryption}" + cryption.encryptText("josiah")+","+privledges;
            Map.Entry<Object, Object> e = new MapEntry<>(propertyName,value);
            User actual = subject.createUserFromEntry(e);
            assertNotNull(actual);
            assertTrue(actual.getPriviledges().contains(priviledge1));
            assertTrue(actual.getPriviledges().contains(priviledge2));
        }

        @Test
        public void testCreate_HasUsers()
        throws Exception
        {
            when(mockSettings.getProperties()).thenReturn(properties);

            String encryptedPassword = new Cryption().encryptText("password");

            String nylaProperty = Cryption.CRYPTION_PREFIX + encryptedPassword + ",admin,read, write ";

            properties.setProperty("security-users.nyla", nylaProperty);

            SettingsUserService subject = new SettingsUserService(mockSettings);

            assertNotNull(subject);
            User user = subject.findUser("invalid");

            assertNull(user);

            user = subject.findUser("nyla");

            assertNotNull(user);
            assertEquals(user.getUserName(), "nyla");

            assertTrue(Arrays.equals(user.getEncryptedPassword(), encryptedPassword.getBytes(StandardCharsets.UTF_8)));

            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("admin"));
            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("read"));
            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("write"));

        }

        @Test
        public void testCreate_UnEncryptedPAsswodHasUsers()
        throws Exception
        {
            when(mockSettings.getProperties()).thenReturn(properties);

            String encryptedPassword = new Cryption().encryptText("password");

            String nylaProperty = Cryption.CRYPTION_PREFIX + encryptedPassword + ",admin,read, write ";

            properties.setProperty("security-users.nyla", nylaProperty);

            SettingsUserService subject = new SettingsUserService(mockSettings);

            assertNotNull(subject);
            User user = subject.findUser("invalid");

            assertNull(user);

            user = subject.findUser("nyla");

            assertNotNull(user);
            assertEquals(user.getUserName(), "nyla");

            assertTrue(Arrays.equals(user.getEncryptedPassword(), encryptedPassword.getBytes(StandardCharsets.UTF_8)));

            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("admin"));
            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("read"));
            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("write"));

        }

        @Test
        public void testGemFirePropertiesCreate_HasUsers()
        throws Exception
        {

            when(mockSettings.getProperties()).thenReturn(properties);
            String encryptedPassword = new Cryption().encryptText("password");
            String nylaProperty = Cryption.CRYPTION_PREFIX + encryptedPassword + ",admin,read, write ";
            properties.setProperty("gemfire.security-users.nyla", nylaProperty);


            SettingsUserService subject = new SettingsUserService(mockSettings);

            assertNotNull(subject);
            User user = subject.findUser("invalid");

            assertNull(user);

            user = subject.findUser("nyla");

            assertNotNull(user);
            assertEquals(user.getUserName(), "nyla");

            assertTrue(Arrays.equals(user.getEncryptedPassword(), encryptedPassword.getBytes(StandardCharsets.UTF_8)));

            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("admin"));
            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("read"));
            assertTrue(user.getPriviledges() != null && user.getPriviledges().contains("write"));


        }//------------------------------------------------

    }

    @Nested
    class WhenFindUser
    {
        @Test
        public void findUser_WhenUserNameHasSpace_ThenReturnUser()
        throws Exception
        {
            String expected = "nyla";
            SettingsUserService subject = new SettingsUserService(settings);
            User actual = subject.findUser(expected+" ");
            assertNotNull(actual);
            assertEquals(expected,actual.getUserName());
        }

        @Test
        public void findUser_WhenUserNameNull_ThenReturnNull()
        throws Exception
        {
            SettingsUserService subject = new SettingsUserService(settings);
            assertNull(subject.findUser(null));
        }
    }

    @Test
    public void test_new_user_in_property_file_authenticated()
    throws Exception
    {
        properties.setProperty("gemfire.security-users.nyla", "{cryption}" + cryption.encryptText("nyla"));
        properties.setProperty("CRYPTION_KEY", "PIVOTAL");
        properties.setProperty("CONFIG_FILE_WATCH_POLLING_INTERVAL_MS", "1000");

        File file = Paths.get("src/test/resources/junit_users.properties").toFile();
        properties.store(new FileWriter(file), null);

        System.setProperty(Config.SYS_PROPERTY, file.getAbsolutePath());
        ConfigSettings settings = new ConfigSettings();


        SettingsUserService subject = new SettingsUserService(settings);
        assertNotNull(subject.findUser("nyla"));
        assertNull(subject.findUser("imani"));

        properties.setProperty("gemfire.security-users.imani", "{cryption}" + cryption.encryptText("imani"));
        properties.store(new FileWriter(file), null);

        System.out.println("Reloading settings");
        settings.reLoad();
        System.out.println("Reloaded settings");
        Thread.sleep(5000);

        System.out.println("Looking for user");
        assertNotNull(subject.findUser("imani"));
        System.out.println("Found for user");

    }

    @Test
    public void test_new_user_in_property_file_privledges()
    throws Exception
    {
        Properties props = new Properties();
        Cryption cryption = new Cryption();
        props.setProperty("gemfire.security-users.nyla", "{cryption}" + cryption.encryptText("nyla") + ",DATA:READ");
        props.setProperty("CRYPTION_KEY", "PIVOTAL");
        props.setProperty("CONFIG_FILE_WATCH_POLLING_INTERVAL_MS","1000");

        File file = Paths.get("src/test/resources/test_new_user_in_property_file_privledges.properties").toFile();
        props.store(new FileWriter(file), null);

        System.setProperty(Config.SYS_PROPERTY, file.getAbsolutePath());
        ConfigSettings settings = new ConfigSettings();


        SettingsUserService configedUser = new SettingsUserService(settings);
        assertTrue(configedUser.findUser("nyla").getPriviledges().contains("DATA:READ"));


        props.setProperty("gemfire.security-users.nyla", "{cryption}" + cryption.encryptText("nyla") + ",DATA:READ,CLUSTER");
        props.store(new FileWriter(file), null);
        System.out.println("reloading");
        settings.reLoad();
        System.out.println("reloaded");

        Thread.sleep(3000);

        System.out.println("finding user");
        User nyla = configedUser.findUser("nyla");
        System.out.println("found user");
        assertTrue(nyla.getPriviledges().contains("DATA:READ"));
        assertTrue(nyla.getPriviledges().contains("CLUSTER"));

    }
}
