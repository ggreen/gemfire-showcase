package com.vmware.spring.gemfire.showcase.account.utils;

import org.apache.geode.cache.client.NoAvailableLocatorsException;
import org.apache.geode.cache.client.NoAvailableServersException;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessResourceFailureException;

public class GemFireQqUtil {
    @NotNull
    public static DataAccessResourceFailureException noAvailableServersException() {
        DataAccessResourceFailureException exception = new DataAccessResourceFailureException("test",new NoAvailableServersException());
        return exception;
    }

    public static DataAccessResourceFailureException noAvailableLocatorsException() {
        return springDataAccess(new NoAvailableLocatorsException());
    }

    private static DataAccessResourceFailureException springDataAccess(Exception e) {
        return new DataAccessResourceFailureException("test",e);
    }
}
