package showcase.gemfire.health.analyzer.runners;

import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.grep.Grep;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LogAnalyzer implements ApplicationRunner {
    private final String directory;

    public LogAnalyzer(@Value("${analyzer.directory}") String directory) {
        this.directory = directory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {


//        Grep.file(directory);
    }

}
