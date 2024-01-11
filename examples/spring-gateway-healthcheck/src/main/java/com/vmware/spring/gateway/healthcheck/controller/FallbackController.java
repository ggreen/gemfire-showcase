package com.vmware.spring.gateway.healthcheck.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class FallbackController {

    private final String httpUrl;
    private final int port;

        private final WebClient webClient;

        public FallbackController( @Value("${gateway.fallback.httpUrl}") String httpUrl,
                                   @Value("${gateway.fallback.port:0}") int port)
        {
            this.httpUrl = httpUrl;
            this.port = port;
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

            log.warn("Falling back from original request {}",originalRequest);

            var newURI = UriComponentsBuilder.fromHttpUrl(
                    this.httpUrl)
                    .port(this.port)
                    .path(originalRequest.getPath().value()).build().toUri();

            log.info("Fallback URI {}",newURI);

            return ForwardedHeaderUtils.adaptFromForwardedHeaders(newURI,
                            originalRequest.getHeaders()).build().toUri();

        }
    }