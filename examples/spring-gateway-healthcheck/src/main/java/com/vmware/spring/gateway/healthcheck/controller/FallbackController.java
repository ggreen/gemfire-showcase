package com.vmware.spring.gateway.healthcheck.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import java.net.URI;
import java.util.Map;

import static java.lang.String.valueOf;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    private final String httpUrl;
    private final int port;

    private final WebClient webClient;

    public FallbackController(@Value("${gateway.fallback.httpUrl}") String httpUrl,
                              @Value("${gateway.fallback.port:0}") int port) {
        this.httpUrl = httpUrl;
        this.port = port;
        this.webClient = WebClient.create();
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

    @PostMapping
    Mono<ResponseEntity<String>> postFallback(@RequestBody(required = false) String body,
                                              ServerWebExchangeDecorator serverWebExchangeDecorator) {
        return fallback(body, serverWebExchangeDecorator);
    }

    private Mono<ResponseEntity<String>> fallback(String body, ServerWebExchangeDecorator serverWebExchangeDecorator) {

        var originalRequest = serverWebExchangeDecorator.getRequest();

        if(body ==  null || body.isEmpty())
            body = valueOf(serverWebExchangeDecorator.getAttributes().get(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR));

        var contentType = valueOf(serverWebExchangeDecorator.getAttributes().get("original_response_content_type"));
        var uri = buildFallbackURI(serverWebExchangeDecorator);

        log.error("***Processing fallback with originalRequestBody: uri: {} ,  body: {}", uri, body);

        var request = webClient.method(originalRequest.getMethod())
                .uri(uri).contentType(MediaType.valueOf(contentType));

        if (body != null) {
            log.error("**Sending with body: {}", body);

            return request.bodyValue(body).exchangeToMono(response -> response.toEntity(String.class));
        } else
            return request.exchangeToMono(response -> response.toEntity(String.class));

    }

    private URI buildFallbackURI(ServerWebExchangeDecorator originalRequest) {

        log.warn("Falling back from original request {}", originalRequest);


        var clientResponse = ((HttpClientResponse)originalRequest.getAttributes().get("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayClientResponse"));

        String uri;
        if(clientResponse == null)
            uri = originalRequest.getDelegate().getRequest().getURI().toString();
        else
            uri = clientResponse.uri();

        Route route = (Route)originalRequest.getAttributes().get("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute");

        var newURI = UriComponentsBuilder.fromHttpUrl(
                        this.httpUrl)
                .port(this.port)
                .path(uri).build().toUri();

        log.info("Fallback URI {}", newURI);

        return ForwardedHeaderUtils.adaptFromForwardedHeaders(newURI,
                toHeaders(clientResponse.requestHeaders())).build().toUri();

    }
//    DefaultHttpHeaders[User-Agent: curl/8.4.0, accept: */*, Forwarded: proto=http;host="localhost:8080";for="[0:0:0:0:0:0:0:1]:63849", X-Forwarded-For: 0:0:0:0:0:0:0:1, X-Forwarded-Proto: http, X-Forwarded-Port: 8080, X-Forwarded-Host: localhost:8080, host: localhost:8181]

    private HttpHeaders toHeaders(io.netty.handler.codec.http.HttpHeaders entries) {
        var headers = new HttpHeaders();
       headers.add("Content-Type",entries.get("Content-Type"));
        return headers;
    }
}