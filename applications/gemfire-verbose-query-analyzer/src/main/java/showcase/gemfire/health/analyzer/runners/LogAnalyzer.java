package showcase.gemfire.health.analyzer.runners;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.io.grep.Grep;
import nyla.solutions.core.io.grep.GrepResult;
import nyla.solutions.core.patterns.decorator.Decorator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import showcase.gemfire.health.analyzer.gc.G1GCReview;

import java.io.File;
import java.nio.file.Paths;

@Component
@Slf4j
public class LogAnalyzer implements ApplicationRunner {
    private final File directory;

    private final File reportDirectory;
    private final Decorator<String, GrepResult> decorator;
    private final File issueFile;
    private final CsvWriter csvWriter;
    private final G1GCReview review;

    @SneakyThrows
    public LogAnalyzer(@Value("${analyzer.directory}") String directory,
                       @Value("${reporting.directory}") String reportDirectoryPath,
                       Decorator<String, GrepResult> decorator, G1GCReview review) {
        this.directory = Paths.get(directory).toFile();
        this.reportDirectory = Paths.get(reportDirectoryPath).toFile();
        this.review = review;
        this.issueFile = Paths.get(this.reportDirectory.getPath()+"/"+"issue.csv").toFile();
        this.issueFile.delete();

        this.decorator = decorator;

        var reportFile = Paths.get(this.reportDirectory.getPath()+"/"+"report.csv").toFile();
        reportFile.delete();

        this.csvWriter = new CsvWriter(reportFile);
        csvWriter.writeHeader("Category","Notes","Evidence");

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        review.reportLogs(directory);

        var serverResults = Grep.file(directory)
                .searchFirstN(
                        line -> (line.contains("[severe") && !line.contains("ack-severe-alert-threshold")) ||
                                line.startsWith("[warn") ||
                                line.startsWith("[error"),
                        Integer.MAX_VALUE

                );

        for(var result : serverResults){

            IO.writeAppend(issueFile,decorator.decorate(result));
            log.info("result: {}",result);
        }

        //Analyzer Results

        var issueResults = Grep.file(issueFile)
                .searchFirst(line -> line.contains("DISTRIBUTED_NO_ACK but enable-network-partition-detection is enabled in the distributed system."));

        if(issueResults != null)
            csvWriter.appendRow("Data Safety","There is a potential inconsistent data when using DISTRIBUTED_NO_ACK for regions",issueResults.results());


    }

}
