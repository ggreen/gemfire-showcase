package com.vmware.spring.gateway.healthcheck.controller.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
@RefreshScope
@ConditionalOnProperty(value="gateway.fallback.enabled", havingValue = "true")
public class GatewayFallbackConfig {
    private String httpUrl;
    private final int port;
    private final Set<String> excludedHeaders;

    public GatewayFallbackConfig(
            @Value("${gateway.fallback.httpUrl}") String httpUrl,
            @Value("${gateway.fallback.port:0}") int port,
            @Value("${gateway.fallback.headers.exclude:}") Set<String> excludedHeaders) {
        this.httpUrl = httpUrl;
        this.port = port;
        this.excludedHeaders = excludedHeaders;
    }

}
