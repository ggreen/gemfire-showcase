package io.pivotal.dataTx.geode.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigAuthInitializeTest {

    private ConfigAuthInitialize subject;

    @BeforeEach
    void setUp() {
        subject = new ConfigAuthInitialize();
    }

    @Test
    void create() {
        assertNotNull(ConfigAuthInitialize.create());
    }

    @Test
    void close() {
        subject.close();
    }

    @Test
    void getCredentials() {
    }

    @Test
    void getSecurityPassword() {
    }

    @Test
    void getSecurityUserName() {
    }

    @Test
    void init() {
    }
}