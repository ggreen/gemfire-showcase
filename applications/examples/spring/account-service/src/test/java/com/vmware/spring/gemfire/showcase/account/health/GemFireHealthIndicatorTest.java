package com.vmware.spring.gemfire.showcase.account.health;

import org.apache.geode.cache.client.NoAvailableServersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.gemfire.GemfireTemplate;

import static com.vmware.spring.gemfire.showcase.account.utils.GemFireQqUtil.noAvailableServersException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GemFireHealthIndicatorTest {

    @Mock
    private GemfireTemplate gemfireTemplate;

    private GemFireHealthIndicator subject;

    @BeforeEach
    void setUp() {
        subject = new GemFireHealthIndicator(gemfireTemplate);
    }

    @DisplayName("GIVEN region with gemfire cluster running WHEN health THEN return up")
    @Test
    void up() {

        var actual = subject.health();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(Health.up().build());
    }


    @DisplayName("GIVEN cause is NoAvailableServersException WHEN health THEN return down")
    @Test
    void down_whenCauseNoAvailableServersException() {
        NoAvailableServersException notServersException = new NoAvailableServersException();
        when(gemfireTemplate.put(anyString(),anyString())).thenThrow(noAvailableServersException());

        var actual = subject.health();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(Health.down().build());
    }

    @DisplayName("GIVEN NoAvailableServersException WHEN health THEN return down")
    @Test
    void down_whenNoAvailableServersException() {
        NoAvailableServersException notServersException = new NoAvailableServersException();
        when(gemfireTemplate.put(anyString(),anyString())).thenThrow(new NoAvailableServersException());

        var actual = subject.health();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(Health.down().build());
    }
}