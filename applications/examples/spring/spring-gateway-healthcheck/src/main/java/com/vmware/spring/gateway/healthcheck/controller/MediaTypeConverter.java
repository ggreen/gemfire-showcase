package com.vmware.spring.gateway.healthcheck.controller;

import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MediaTypeConverter {
    private final String defaultContentType;

    public MediaTypeConverter(@Value("${gateway.fallback.default.mediaType:application/json}") String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }


    public String toMimeType(Object contentType) {
        var  contentTypeText = String.valueOf(contentType);

        if(Text.isNull(contentTypeText))
            return defaultContentType;

        return contentTypeText;
    }
}
