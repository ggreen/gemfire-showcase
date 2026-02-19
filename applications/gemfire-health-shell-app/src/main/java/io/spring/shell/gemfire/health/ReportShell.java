package io.spring.shell.gemfire.health;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;
import com.vmware.data.services.gemfire.operations.stats.visitors.StatsVisitor;
import com.vmware.pivotal.labs.services.dataTx.geode.office.*;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.office.chart.Chart;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Shell wrapping for assessment commands
 * @author Gregory Green
 */
@Component
class ReportShell
{
    private String datePattern = "uuuu-M-d";

    @Command("Converts statistics to CSV")
    public void csv(String statsFileOrDirPath,
            String dayYYYYMMDDFilter,
            String outfileCsvFile ) throws IOException {

        File statFileOrDirectory = Paths.get(statsFileOrDirPath).toFile();


        if (outfileCsvFile.isEmpty())
            throw new IllegalArgumentException("outfileCsvFile required");


        CsvWriter csvWriter  = new CsvWriter(Paths.get(outfileCsvFile).toFile());
        CsvVisitor visitor = new CsvVisitor(
                csvWriter,
                toDay(dayYYYYMMDDFilter));


        if (statFileOrDirectory.isDirectory()) {
            Set<File> statFiles = IO.dir().listFileRecursive(statFileOrDirectory, "*.gfs");

            for (File statFile: statFiles) {
                GfStatsReader reader = new GfStatsReader(statFile);

                reader.acceptVisitors((StatsVisitor) visitor);
            }

        }
        else{
            GfStatsReader reader = new GfStatsReader(statFileOrDirectory);

            reader.acceptVisitors((StatsVisitor) visitor);
        }

    }

    @Command("Builds a chart of the MAX JVM Memory Over a Threshold")
    public void chartJvmMaxMemoryAboveThreshold(String outFileImagePath, String inputFilePathDir, @Option(defaultValue = "50") int memoryPercentage) throws IOException {
        File jvmMemoryFilePath = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        var v = new JvmMaxHeapUsageAboveThresholdChartStatsVisitor();

        v.setPercentThreshold(memoryPercentage);
        var jvmMemoryChart  = new  StatsToChart(v);
        var chart = jvmMemoryChart.convert(inputFileOrDirectory);

        IO.writer().writeFile(jvmMemoryFilePath, chart.getBytes());

    }

    @Command("Builds a chart of the CPU Usage")
    public void chartCpuUsage(String outFileImagePath,
                      String inputFilePathDir ,
                      String dayFilter,
                      @Option(defaultValue = "50") Integer cpuUsageThreshold) throws IOException {
        var jvmMemoryFilePath  = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        var v = new CpuAboveThresholdChartStatsVisitor(new Day(dayFilter), cpuUsageThreshold.doubleValue());

        var chart  =  new StatsToChart(v)
            .convert(inputFileOrDirectory);

        IO.writer().writeFile(jvmMemoryFilePath, chart.getBytes());

    }

    @Command("Builds a chart of the AVG JVM Memory Over a Threshold")
    public void  chartJvmAvgMemoryAboveThreshold(String outFileImagePath,
                                                 String inputFilePathDir,
                                                 @Option(defaultValue = "50") int memoryPercentage ) throws IOException {
        File jvmMemoryFilePath  =new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        Chart jvmMemoryChart  =  new StatsToChart(new JvmAvgHeapUsageAboveThresholdChartStatsVisitor())
            .convert(inputFileOrDirectory);

        IO.writer().writeFile(jvmMemoryFilePath, jvmMemoryChart.getBytes());

    }

    @Command("Builds a chart ParNew Garbage Collections")
    public void  chartParNewCollections(String outFileImagePath, String inputFilePathDir, String dayDate ) throws IOException {
        File outFilePath  = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        Chart chart  =  new StatsToChart(new ParNewCollectionTimeThresholdChartStatsVisitor(new Day(dayDate)))
            .convert(inputFileOrDirectory);

        IO.writer().writeFile(outFilePath, chart.getBytes());

    }

    @Command("Builds a chart ParNew Collections times over a duration")
    public void  chartParNewCollectionTimeThreshold(String outFileImagePath ,
                                                    String inputFilePathDir,
                                                    @Option(defaultValue = "50")  Double thresholdMs,
                                                    String dayDate ) throws IOException {
        File outFilePath  = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        Chart chart  =  new StatsToChart(new ParNewCollectionTimeThresholdChartStatsVisitor(new Day(dayDate),thresholdMs))
            .convert(inputFileOrDirectory);

        IO.writer().writeFile(outFilePath, chart.getBytes());

    }


    private LocalDate toLocalDate(String dayYYYYMMDDFilter) {
    return LocalDate.parse(
            dayYYYYMMDDFilter,
            DateTimeFormatter
                    .ofPattern(datePattern));
}
    private Day toDay(String dayYYYYMMDDFilter) {
        return new Day(toLocalDate(dayYYYYMMDDFilter));
    }

}