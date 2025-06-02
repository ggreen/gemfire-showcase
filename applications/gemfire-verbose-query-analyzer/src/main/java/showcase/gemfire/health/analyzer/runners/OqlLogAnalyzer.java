package showcase.gemfire.health.analyzer.runners;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Component
@Slf4j
public class OqlLogAnalyzer implements ApplicationRunner {
    private final File directory;

    private final File reportDirectory;
    private final File queryTimesFile;
    private final CsvWriter csvWriter;

    @SneakyThrows
    public OqlLogAnalyzer(@Value("${analyzer.directory}") String directory,
                          @Value("${reporting.directory}") String reportDirectoryPath,
                          CsvWriter csvWriter) {
        this.directory = Paths.get(directory).toFile();
        this.reportDirectory = Paths.get(reportDirectoryPath).toFile();
        this.csvWriter = csvWriter;
        this.queryTimesFile = Paths.get(this.reportDirectory.getPath() + "/" + "oql.csv").toFile();

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

       var listFiles = IO.listFileRecursive(this.directory.getAbsolutePath(),"*.log");

       for(File file : listFiles){
           try (Stream<String> lines = Files.lines(file.toPath())) {

               var osql = new AtomicReference<String>("");
               var ms = new AtomicReference<String>("");

               lines.forEach(line -> {

                   var formattedLine= line.toUpperCase();
                   if(formattedLine.contains("OQL:"))
                   {
                       if(formattedLine.contains("SELECT")&& formattedLine.contains("FROM"))
                           osql.set(Text.parseRE(line, "OQL:", "$"));

                       else if(formattedLine.endsWith("MS")){
                           ms.set(Text.parseRE(line, " rows, ", "ms$"));

                           try {
                               csvWriter.appendRow(osql.get(),ms.get());
                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           }
                       }
                   }

               });
           }
       }



    }

}
