package com.vmware.spring.gateway.healthcheck.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTypeConverterTest {
    private MediaTypeConverter subject;

    @BeforeEach
    void setUp() {
        subject = new MediaTypeConverter("application/json");
    }

    @Test
    void toMimeType() {

        assertThat(subject.toMimeType(null)).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(subject.toMimeType("")).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(subject.toMimeType("null")).isEqualTo(MediaType.APPLICATION_JSON.toString());

        assertThat(subject.toMimeType("text/plain")).isEqualTo(MediaType.TEXT_PLAIN.toString());

    }
}