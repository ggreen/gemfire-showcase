package com.vmware.data.services.gemfire.client;

import nyla.solutions.core.exception.RequiredException;
import nyla.solutions.core.exception.SetupException;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Organizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GemFireClientBuilderTest
{

    private GemFireClientBuilder subject;

    @BeforeEach
    void setUp()
    {
        subject = new GemFireClientBuilder();

    }


    @Test
    void given_locators_whenGetUris_returnListOfUrls() throws URISyntaxException
    {
        List<URI> expected = Organizer.toList(new URI(("locator://gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local:10334")),
                new URI(("locator://gemfire1-locator-1.gemfire1-locator.default.svc.cluster.local:10334")));

        String locators = "gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local[10334],gemfire1-locator-1.gemfire1-locator.default.svc.cluster.local[10334]";
        subject.locators(locators);
        List<URI> actual = subject.getUris();

        assertEquals(expected,actual);

    }

    @Test
    void given_no_locators_set_WhenGetUris_Then_exception() throws URISyntaxException
    {
        List<URI> expected = Organizer.toList(new URI(("locator://localhost:10334")));
        assertThrows(RequiredException.class,() -> subject.getUris());
    }

    @Test
    void given_connectionLocatorsNameCredential_whenSet_then_Matches()
    {
        String locators = "host:14333";

        String clientName = "app";

        String userName = "user";

        char[] password = "sdsds".toCharArray();

        subject.locators(locators)
                       .clientName(clientName)
                               .userName(userName)
                       .password(password);

        assertEquals(locators,subject.getLocators());
        assertEquals(clientName,subject.getClientName());
        assertEquals(userName,subject.getUserName());
        assertEquals("sdsds", String.valueOf(subject.getPassword()));

    }

    @Test
    void builderPassword_Then_return_notNull() {

        Builder actual = subject.password("password".toCharArray());
        assertNotNull(actual);

        assertEquals("password",Config.settings().getProperty(GemFireConfigAuthInitialize.SECURITY_PASSWORD_PROP));

    }
}