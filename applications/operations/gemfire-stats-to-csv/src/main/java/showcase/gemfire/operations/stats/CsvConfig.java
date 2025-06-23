package showcase.gemfire.operations.stats;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.gemfire.operations.stats.visitor.CsvVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class CsvConfig {

    @Value("${csv.output.file}")
    private String filePath;

    @Value("${stats.input.path}")
    private String inputStatusFileDirectory;

    @Value("${stats.day.filter}")
    private String dayFilter;

    @Bean
    Day dayFilter()
    {
        return new Day(dayFilter);
    }

    @Bean
    CsvWriter csvWriter()
    {
        return  new CsvWriter(Paths.get(filePath).toFile());
    }

    @Bean
    CsvVisitor csvVisitor(CsvWriter csvWriter, Day day) throws IOException {
        return new CsvVisitor(csvWriter,day);
    }

    @Bean
    CommandLineRunner runner(CsvVisitor csvVisitor)
    {
        return args -> {

            var files = IO.listFileRecursive(inputStatusFileDirectory,"*.gfs");

            for(File file : files){
                var reader = new GfStatsReader(file);
                reader.acceptVisitors(csvVisitor);
            }
        };
    }

}
