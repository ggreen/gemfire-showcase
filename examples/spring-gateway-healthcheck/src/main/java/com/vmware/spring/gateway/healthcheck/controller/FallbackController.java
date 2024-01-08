package com.vmware.spring.gateway.healthcheck.controller;

import com.vmware.spring.gateway.healthcheck.controller.config.GatewayFallbackConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/fallback")
@ConditionalOnProperty(value="gateway.fallback.enabled", havingValue = "true")
public class FallbackController {

        private final GatewayFallbackConfig gatewayFallbackConfig;

        private final WebClient webClient;

        public FallbackController(GatewayFallbackConfig gatewayFallbackConfig) {
            this.gatewayFallbackConfig = gatewayFallbackConfig;
            this.webClient = WebClient.create();
        }

        @PostMapping
        Mono<ResponseEntity<String>> postFallback(@RequestBody(required = false) String body,
                                                  ServerWebExchangeDecorator serverWebExchangeDecorator) {
            return fallback(body, serverWebExchangeDecorator);
        }

        @GetMapping
        Mono<ResponseEntity<String>> getFallback(@RequestBody(required = false) String body,
                                                 ServerWebExchangeDecorator serverWebExchangeDecorator) {
            return fallback(body, serverWebExchangeDecorator);
        }

        @PatchMapping
        Mono<ResponseEntity<String>> patchFallback(@RequestBody(required = false) String body,
                                                   ServerWebExchangeDecorator serverWebExchangeDecorator) {
            return fallback(body, serverWebExchangeDecorator);
        }

        @DeleteMapping
        Mono<ResponseEntity<String>> deleteFallback(@RequestBody(required = false) String body,
                                                    ServerWebExchangeDecorator serverWebExchangeDecorator) {
            return fallback(body, serverWebExchangeDecorator);
        }

        private Mono<ResponseEntity<String>> fallback(String body, ServerWebExchangeDecorator serverWebExchangeDecorator) {
            var originalRequest = serverWebExchangeDecorator.getDelegate().getRequest();

            var request = webClient.method(originalRequest.getMethod())
                    .uri(buildFallbackURI(originalRequest));

            Optional.ofNullable(body)
                    .ifPresent(request::bodyValue);

            return request.exchangeToMono(response -> response.toEntity(String.class));
        }

        private URI buildFallbackURI(ServerHttpRequest originalRequest) {

            var newURI = UriComponentsBuilder.fromHttpUrl(
                    gatewayFallbackConfig.getHttpUrl())
                    .port(gatewayFallbackConfig.getPort())
                    .path(originalRequest.getPath().value()).build().toUri();

            return ForwardedHeaderUtils.adaptFromForwardedHeaders(newURI,
                            originalRequest.getHeaders()).build().toUri();

        }
    }