package showcase.gemfire.health.analyzer;

import nyla.solutions.core.io.csv.CsvWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class G1GCConfig {

    @Value("${reporting.directory}")
    private String reportDirectory;

    @Bean
    CsvWriter gcCsvWriter()
    {
        File file = Paths.get(reportDirectory+"/gc.csv").toFile();
        file.delete();
        return new CsvWriter(file);
    }
}
