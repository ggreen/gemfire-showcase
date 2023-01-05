package io.pivotal.service.dataTx.geode.health

import com.vmware.pivotal.labs.services.dataTx.geode.office.*
import io.pivotal.services.dataTx.geode.health.api.dao.StatDao
import io.pivotal.services.dataTx.geode.health.api.dao.entity.JpaEntityManagerFactory
import io.pivotal.services.dataTx.geode.health.api.dao.entity.StatDbType
import io.pivotal.services.dataTx.geode.health.api.reporting.HtmlStatsGeodeReporter
import io.pivotal.services.dataTx.geode.health.api.reporting.database.StatsToDatabaseVisitor
import io.pivotal.services.dataTx.geode.health.api.reporting.domain.ReportingSetting
import io.pivotal.services.dataTx.geode.operations.stats.GfStatsReader
import nyla.solutions.core.data.clock.Day
import nyla.solutions.core.io.IO
import nyla.solutions.core.io.csv.CsvWriter
import nyla.solutions.core.util.Text
import nyla.solutions.office.chart.Chart
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Shell wrapping for assessment commands
 * @author Gregory Green
 */
@ShellComponent
class ReportShell
{
    private var datePattern = "uuuu-M-d";

    @ShellMethod("Converts statistics to CSV")
    fun csv(statsFileOrDirPath: String,
            dayYYYYMMDDFilter: String,
            outfileCsvFile : String) {

        var statFileOrDirectory = Paths.get(statsFileOrDirPath).toFile();


        if (outfileCsvFile.isEmpty())
            throw IllegalArgumentException("outfileCsvFile required");


        var csvWriter : CsvWriter = CsvWriter(Paths.get(outfileCsvFile).toFile());
        var visitor = CsvVisitor(
                csvWriter,
                toDay(dayYYYYMMDDFilter));


        if (statFileOrDirectory.isDirectory()) {
            var statFiles = IO.listFileRecursive(statFileOrDirectory, "*.gfs");

            for (statFile in statFiles) {
                var reader: GfStatsReader = GfStatsReader(statFile);
                reader.acceptVisitors(visitor);
            }

        }
        else{
            var reader: GfStatsReader = GfStatsReader(statFileOrDirectory);

            reader.acceptVisitors(visitor);
        }

    }//-------------------------------------------

    @ShellMethod("Builds a complete report based on statistic files")
    fun htmlReport(statsFileOrDirPath : String,
                   reportFilePath: String,
                   dayFilter: String,
                   @ShellOption(defaultValue = "") customTemplateFile : String,
                   @ShellOption(defaultValue = "30")  avgMemoryHeapThreshold : Int = 30,
                   @ShellOption(defaultValue = "30")  maxMemoryHeapThreshold : Int = 30,
                   @ShellOption(defaultValue = "30") cpuUsageThreshold: Int = 30,
                   @ShellOption(defaultValue = "1") connectionTimeoutsThreshold :Int = 1,
                   @ShellOption(defaultValue = "1")  abandonedReadRequestsThreshold: Int = 1,
                   @ShellOption(defaultValue = "1")  failedConnectionAttemptsThreshold: Int = 1,
                   @ShellOption(defaultValue = "10")  ioWaitThreshold : Int = 10
                   )
    {
        var settings = ReportingSetting()
        settings.avgMemoryThreshold = avgMemoryHeapThreshold;
        settings.maxMemoryThreshold = maxMemoryHeapThreshold;
        settings.cpuUsageThreshold = cpuUsageThreshold;
        settings.connectionTimeoutsThreshold  = connectionTimeoutsThreshold;
        settings.abandonedReadRequestsThreshold = abandonedReadRequestsThreshold;
        settings.failedConnectionAttemptsThreshold = failedConnectionAttemptsThreshold;
        settings.ioWaitThreshold = ioWaitThreshold;
        settings.dayFilter = Day(dayFilter);

        val reporter = HtmlStatsGeodeReporter(settings)


        val statsFileOrDir = Paths.get(statsFileOrDirPath).toFile()

        if(customTemplateFile.length == 0)
        {
            var loadedTemplate =Text.loadTemplate("html");
            reporter.report(statsFileOrDir, File(reportFilePath), loadedTemplate);
        }
        else
        {
            var template = IO.readFile(customTemplateFile);

            reporter.report(statsFileOrDir, File(reportFilePath), template);
        }

    }//------------------------------------------------------------------------

    @ShellMethod("Builds a chart of the MAX JVM Memory Over a Threshold")
    fun chartJvmMaxMemoryAboveThreshold(outFileImagePath: String, inputFilePathDir : String, @ShellOption(defaultValue = "50") memoryPercentage : Int)
    {
        var jvmMemoryFilePath : File = File(outFileImagePath)
        var inputFileOrDirectory = File(inputFilePathDir)

        var v = JvmMaxHeapUsageAboveThresholdChartStatsVisitor()
        v.setPercentThreshold(memoryPercentage)
        var jvmMemoryChart : Chart =  StatsToChart(v)
                .convert(inputFileOrDirectory);

        IO.writeFile(jvmMemoryFilePath, jvmMemoryChart.getBytes())

    }//-------------------------------------------


    @ShellMethod("Builds a chart of the CPU Usage")
    fun chartCpuUsage(outFileImagePath: String,
                      inputFilePathDir : String,
                      dayFilter: String,
                      @ShellOption(defaultValue = "50") cpuUsageThreshold : Int = 50)
    {
        var jvmMemoryFilePath : File = File(outFileImagePath)
        var inputFileOrDirectory = File(inputFilePathDir)

        var v = CpuAboveThresholdChartStatsVisitor(Day(dayFilter), cpuUsageThreshold.toDouble());

        var chart : Chart =  StatsToChart(v)
                .convert(inputFileOrDirectory);

        IO.writeFile(jvmMemoryFilePath, chart.getBytes())

    }//-------------------------------------------

    @ShellMethod("Builds a chart of the AVG JVM Memory Over a Threshold")
    fun chartJvmAvgMemoryAboveThreshold(outFileImagePath: String, inputFilePathDir : String, @ShellOption(defaultValue = "50") memoryPercentage : Int)
    {
        var jvmMemoryFilePath : File = File(outFileImagePath)
        var inputFileOrDirectory = File(inputFilePathDir)

        var jvmMemoryChart : Chart =  StatsToChart(JvmAvgHeapUsageAboveThresholdChartStatsVisitor())
                .convert(inputFileOrDirectory);

        IO.writeFile(jvmMemoryFilePath, jvmMemoryChart.getBytes())

    }//-------------------------------------------

    @ShellMethod("Builds a chart ParNew Garbage Collections")
    fun chartParNewCollections(outFileImagePath: String, inputFilePathDir :String, dayDate : String)
    {
        var outFilePath : File = File(outFileImagePath)
        var inputFileOrDirectory = File(inputFilePathDir)

        var chart : Chart =  StatsToChart(ParNewCollectionTimeThresholdChartStatsVisitor(Day(dayDate)))
                .convert(inputFileOrDirectory);

        IO.writeFile(outFilePath, chart.getBytes())

    }//-------------------------------------------
    @ShellMethod("Builds a chart ParNew Collections times over a duration")
    fun chartParNewCollectionTimeThreshold(outFileImagePath: String, inputFilePathDir :String, @ShellOption(defaultValue = "50")  thresholdMs: Double, dayDate : String)
    {
        var outFilePath : File = File(outFileImagePath)
        var inputFileOrDirectory = File(inputFilePathDir)

        var chart : Chart =  StatsToChart(ParNewCollectionTimeThresholdChartStatsVisitor(Day(dayDate),thresholdMs))
                .convert(inputFileOrDirectory);

        IO.writeFile(outFilePath, chart.getBytes())

    }//-------------------------------------------
    @ShellMethod("Saves stats to database")
    fun dbSync(statsFileOrDirPath: String,
               jdbcDbType: StatDbType,
               jdbcUrl: String,
               jdbcUsername: String,
               jdbcPassword: String,
               @ShellOption()
               dayYYYYMMDDFilter: String,
               @ShellOption(defaultValue = "")
               statTypeName : String,
               @ShellOption(defaultValue = "")
               statName : String,
               @ShellOption(defaultValue = "1000")
               batchSize : Int) {


        var factory = JpaEntityManagerFactory
                .builder()
                .statDbType(jdbcDbType)
                .jdbcUrl(jdbcUrl)
                .jdbcUsername(jdbcUsername)
                .jdbcPassword(jdbcPassword)
                .batchSize(batchSize)
                .build();

        var dao = StatDao(factory.entityManager);


        var file = Paths.get(statsFileOrDirPath).toFile();
        try {
            var visitor : StatsToDatabaseVisitor = StatsToDatabaseVisitor
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
                    val statsFiles: Set<File> = IO.listFileRecursive(file, "*.gfs")

                    for (statFile in statsFiles) {
                        val reader = GfStatsReader(statFile.absolutePath)
                        reader.acceptVisitors(visitor)
                    }
                } else {
                    val reader = GfStatsReader(file.getAbsolutePath())
                    reader.acceptVisitors(visitor)
                }
            }
        }
        catch(e : DateTimeParseException)
        {
            throw java.lang.IllegalArgumentException(
                    "Date Filter:${dayYYYYMMDDFilter} does not match expected date format: ${datePattern} ");
        }


    }//-------------------------------------------
    private fun toLocalDate(dayYYYYMMDDFilter: String): LocalDate {
        return LocalDate.parse(
                dayYYYYMMDDFilter,
                DateTimeFormatter
                        .ofPattern(datePattern))
    }
    private fun toDay(dayYYYYMMDDFilter: String): Day {
        return Day(toLocalDate(dayYYYYMMDDFilter))
    }

}