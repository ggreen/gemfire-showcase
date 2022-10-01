package com.vmware.data.services.gemfire.integration.jdbc;

import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.postgresql.Driver;

import javax.sql.ConnectionPoolDataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HikariDataSourceCreatorTest
{
    @Test
    @EnabledIfSystemProperty(named = "integration.test", matches = "true")
    void create() throws Exception
    {
        ConnectionPoolDataSource p;
        System.setProperty("CRYPTION_KEY","JUNIT_TESTING");
        System.setProperty("JDBC_URL","jdbc:h2:~/test");
        System.setProperty("JDBC_DRIVER_CLASS", org.h2.Driver.class.getName());
        System.setProperty("JDBC_USERNAME","test");
        System.setProperty("JDBC_PASSWORD", Cryption.CRYPTION_PREFIX+String.valueOf(new Cryption("JUNIT_TESTING").encryptText("test")));

        Config.reLoad();

        HikariDataSourceCreator subject = new HikariDataSourceCreator();

        assertNotNull(subject.create());
    }

    @Test
    @EnabledIfSystemProperty(named = "integration.test", matches = "true")
    void create_Postgres() throws Exception
    {
        System.setProperty("CRYPTION_KEY","JUNIT_TESTING");
        System.setProperty("JDBC_DRIVER_CLASS", Driver.class.getName());
        System.setProperty("JDBC_URL","jdbc:postgresql://localhost:5432/postgres");
        System.setProperty("JDBC_USERNAME","postgres");
        System.setProperty("JDBC_PASSWORD", Cryption.CRYPTION_PREFIX+String.valueOf(new Cryption("JUNIT_TESTING").encryptText("security")));

        Class.forName(Driver.class.getName());

        Config.reLoad();

        HikariDataSourceCreator subject = new HikariDataSourceCreator();

        assertNotNull(subject.create());
    }
}