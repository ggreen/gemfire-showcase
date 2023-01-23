package io.spring.shell.gemfire.health;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;
import com.vmware.data.services.gemfire.operations.stats.visitors.StatsVisitor;
import com.vmware.pivotal.labs.services.dataTx.geode.office.*;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.office.chart.Chart;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

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
@ShellComponent
class ReportShell
{
    private String datePattern = "uuuu-M-d";

    @ShellMethod("Converts statistics to CSV")
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
            Set<File> statFiles = IO.listFileRecursive(statFileOrDirectory, "*.gfs");

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
/*
    @ShellMethod("Builds a complete report based on statistic files")
    public void htmlReport(String statsFileOrDirPath ,
                           String reportFilePath,
                           String dayFilter,
                   @ShellOption(defaultValue = "") String customTemplateFile ,
                   @ShellOption(defaultValue = "30")  int avgMemoryHeapThreshold,
                   @ShellOption(defaultValue = "30") int maxMemoryHeapThreshold,
                   @ShellOption(defaultValue = "30") int  cpuUsageThreshold,
                   @ShellOption(defaultValue = "1")  int connectionTimeoutsThreshold,
                   @ShellOption(defaultValue = "1")   int abandonedReadRequestsThreshold,
                   @ShellOption(defaultValue = "1")   int failedConnectionAttemptsThreshold,
                   @ShellOption(defaultValue = "10")   int ioWaitThreshold
    ) throws IOException {
        var settings = new ReportingSetting();
        settings.avgMemoryThreshold = avgMemoryHeapThreshold;
        settings.maxMemoryThreshold = maxMemoryHeapThreshold;
        settings.cpuUsageThreshold = cpuUsageThreshold;
        settings.connectionTimeoutsThreshold  = connectionTimeoutsThreshold;
        settings.abandonedReadRequestsThreshold = abandonedReadRequestsThreshold;
        settings.failedConnectionAttemptsThreshold = failedConnectionAttemptsThreshold;
        settings.ioWaitThreshold = ioWaitThreshold;
        settings.dayFilter = Day(dayFilter);

        HtmlStatsGeodeReporter reporter = HtmlStatsGeodeReporter(settings)


        File statsFileOrDir = Paths.get(statsFileOrDirPath).toFile();

        if(customTemplateFile.length() == 0)
        {
            var loadedTemplate =Text.loadTemplate("html");
            reporter.report(statsFileOrDir, File(reportFilePath), loadedTemplate);
        }
        else
        {
            var template = IO.readFile(customTemplateFile);

            reporter.report(statsFileOrDir, File(reportFilePath), template);
        }

    }*/

    @ShellMethod("Builds a chart of the MAX JVM Memory Over a Threshold")
    public void chartJvmMaxMemoryAboveThreshold(String outFileImagePath, String inputFilePathDir, @ShellOption(defaultValue = "50") int memoryPercentage) throws IOException {
        File jvmMemoryFilePath = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        var v = new JvmMaxHeapUsageAboveThresholdChartStatsVisitor();

        v.setPercentThreshold(memoryPercentage);
        var jvmMemoryChart  = new  StatsToChart(v);
        var chart = jvmMemoryChart.convert(inputFileOrDirectory);

        IO.writeFile(jvmMemoryFilePath, chart.getBytes());

    }

    @ShellMethod("Builds a chart of the CPU Usage")
    public void chartCpuUsage(String outFileImagePath,
                      String inputFilePathDir ,
                      String dayFilter,
                      @ShellOption(defaultValue = "50") Integer cpuUsageThreshold) throws IOException {
        var jvmMemoryFilePath  = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        var v = new CpuAboveThresholdChartStatsVisitor(new Day(dayFilter), cpuUsageThreshold.doubleValue());

        var chart  =  new StatsToChart(v)
            .convert(inputFileOrDirectory);

        IO.writeFile(jvmMemoryFilePath, chart.getBytes());

    }

    @ShellMethod("Builds a chart of the AVG JVM Memory Over a Threshold")
    public void  chartJvmAvgMemoryAboveThreshold(String outFileImagePath,
                                                 String inputFilePathDir,
                                                 @ShellOption(defaultValue = "50") int memoryPercentage ) throws IOException {
        File jvmMemoryFilePath  =new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        Chart jvmMemoryChart  =  new StatsToChart(new JvmAvgHeapUsageAboveThresholdChartStatsVisitor())
            .convert(inputFileOrDirectory);

        IO.writeFile(jvmMemoryFilePath, jvmMemoryChart.getBytes());

    }//-------------------------------------------

    @ShellMethod("Builds a chart ParNew Garbage Collections")
    public void  chartParNewCollections(String outFileImagePath, String inputFilePathDir, String dayDate ) throws IOException {
        File outFilePath  = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        Chart chart  =  new StatsToChart(new ParNewCollectionTimeThresholdChartStatsVisitor(new Day(dayDate)))
            .convert(inputFileOrDirectory);

        IO.writeFile(outFilePath, chart.getBytes());

    }

    @ShellMethod("Builds a chart ParNew Collections times over a duration")
    public void  chartParNewCollectionTimeThreshold(String outFileImagePath ,
                                                    String inputFilePathDir,
                                                    @ShellOption(defaultValue = "50")  Double thresholdMs,
                                                    String dayDate ) throws IOException {
        File outFilePath  = new File(outFileImagePath);
        var inputFileOrDirectory = new File(inputFilePathDir);

        Chart chart  =  new StatsToChart(new ParNewCollectionTimeThresholdChartStatsVisitor(new Day(dayDate),thresholdMs))
            .convert(inputFileOrDirectory);

        IO.writeFile(outFilePath, chart.getBytes());

    }

    /*@ShellMethod("Saves stats1  22121 to database")
    public void  dbSync(String statsFileOrDirPath,
                        StatDbType jdbcDbType,
                        String jdbcUrl,
                        String jdbcUsername,
                        String jdbcPassword,
               @ShellOption()
                        String dayYYYYMMDDFilter,
               @ShellOption(defaultValue = "")
                        String statTypeName ,
               @ShellOption(defaultValue = "")
                        String statName ,
               @ShellOption(defaultValue = "1000")
               int batchSize ) throws IOException {


        var factory = new JpaEntityManagerFactory
                .builder()
                .statDbType(jdbcDbType)
                .jdbcUrl(jdbcUrl)
                .jdbcUsername(jdbcUsername)
                .jdbcPassword(jdbcPassword)
                .batchSize(batchSize)
                .build();

        var dao = new StatDao(factory.entityManager);


        var file = Paths.get(statsFileOrDirPath).toFile();
        try {
            StatsToDatabaseVisitor visitor  = new StatsToDatabaseVisitor
                    .builder()
                    .batchSize(batchSize)
                    .dao(dao)
                    .dayFilter(
                            toLocalDate(dayYYYYMMDDFilter))
                    .statTypeName(statTypeName)
                    .statName(statName)
                    .build();

            dao.use {

                if (file.isDirectory()) { //Process for all files
                    Set<File> statsFiles = IO.listFileRecursive(file, "*.gfs")

                    for (File statFile : statsFiles) {
                        GfStatsReader reader = new GfStatsReader(statFile.getAbsolutePath())
                        reader.acceptVisitors(visitor)
                    }
                } else {
                    var reader = new GfStatsReader(file.getAbsolutePath())
                    reader.acceptVisitors(visitor);
                }
            }
        }
        catch( DateTimeParseException e )
        {
            throw new IllegalArgumentException(
                    "Date Filter:${dayYYYYMMDDFilter} does not match expected date format: ${datePattern} ");
        }


    }*/

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