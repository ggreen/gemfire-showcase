package showcase.gemfire.health.analyzer;

import lombok.SneakyThrows;
import nyla.solutions.core.io.csv.CsvWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class OqlCsvConfig {

    @Value("${reporting.directory}")
    private String reportDirectory;

    @SneakyThrows
    @Bean
    CsvWriter gcCsvWriter()
    {
        File file = Paths.get(reportDirectory+"/oql.csv").toFile();
        file.delete();
        var writer = new CsvWriter(file);
        writer.writeHeader("oql","Time(Ms)");
        return writer;
    }
}
