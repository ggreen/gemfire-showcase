package io.spring.gemfire.perftest;

import nyla.solutions.core.data.json.JsonSchemaBluePrint;
import nyla.solutions.core.operations.performance.BenchMarker;
import nyla.solutions.core.patterns.creational.generator.json.JsonGeneratorCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Configuration
public class PerfConfig {

    @Value("${action}")
    String action = "putString";

    @Value("${threadCount}")
    int threadCount;

    @Value("${threadSleepMs}")
    long threadSleepMs;

    @Value("${rampUPSeconds}")
    int rampUPSeconds;

    @Value("${loopCount}")
    Long loopCount;

    @Value("${threadLifeTimeSeconds}")
    long threadLifeTimeSeconds;

    @Bean
    PropertiesFactoryBean jsonSchemaProperties(){
        var bean = new PropertiesFactoryBean();

        bean.setLocation(new ClassPathResource("json-schema.properties"));
        return bean;
    }

    @Bean
    JsonGeneratorCreator jsonGenerator(Properties jsonSchemaProperties) {
        var schema = new JsonSchemaBluePrint(DateTimeFormatter.ISO_DATE_TIME,
                jsonSchemaProperties);

        return new JsonGeneratorCreator(schema);
    }

    @Bean
    BenchMarker benchMark() {
        return BenchMarker.builder()
                .threadCount(threadCount)
                .threadSleepMs(threadSleepMs)
                .rampUPSeconds(rampUPSeconds)
                .loopCount(loopCount)
                .threadLifeTimeSeconds(threadLifeTimeSeconds)
                .build();
    }
}
