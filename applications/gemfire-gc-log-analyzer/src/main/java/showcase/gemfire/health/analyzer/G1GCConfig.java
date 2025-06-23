package showcase.gemfire.health.analyzer;

import nyla.solutions.core.io.csv.CsvWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.gemfire.health.analyzer.gc.G1GCReview;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

@Configuration
public class G1GCConfig {

    @Value("${reporting.directory}")
    private String reportDirectory;

    @Value("${input.gc.logs.directory}")
    private String directory;

    @Bean
    CsvWriter gcCsvWriter()
    {
        File file = Paths.get(reportDirectory+"/gc.csv").toFile();
        file.delete();
        return new CsvWriter(file);
    }

    @Bean
    CommandLineRunner runner(G1GCReview review){
        return args -> review.reportLogs(Paths.get(directory).toFile());
    }

}
