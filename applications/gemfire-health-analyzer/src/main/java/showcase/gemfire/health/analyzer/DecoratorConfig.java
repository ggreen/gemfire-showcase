package showcase.gemfire.health.analyzer;

import nyla.solutions.core.io.grep.GrepResult;
import nyla.solutions.core.io.grep.GrepResultCsvDecorator;
import nyla.solutions.core.patterns.decorator.Decorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DecoratorConfig {

    @Bean
    Decorator<String, GrepResult> decorator()
    {
        return new GrepResultCsvDecorator();
    }
}
