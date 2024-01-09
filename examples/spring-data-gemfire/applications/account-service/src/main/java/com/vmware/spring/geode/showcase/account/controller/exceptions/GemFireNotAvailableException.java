package com.vmware.spring.geode.showcase.account.controller.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponseException;

public class GemFireNotAvailableException extends ErrorResponseException {

    public static final int GEMFIRE_NOT_AVAILABLE = 610;

    public GemFireNotAvailableException(Throwable cause) {
        super(HttpStatusCode.valueOf(GEMFIRE_NOT_AVAILABLE), cause);
    }

    public GemFireNotAvailableException() {
        super(HttpStatusCode.valueOf(GEMFIRE_NOT_AVAILABLE));
    }
}
