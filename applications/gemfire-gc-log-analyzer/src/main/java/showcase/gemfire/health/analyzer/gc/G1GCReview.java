package showcase.gemfire.health.analyzer.gc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import static nyla.solutions.core.util.Debugger.println;

@Component
@Slf4j
public class G1GCReview {

    private static final Pattern gcPausePattern = Pattern.compile(
            "\\[(.*?)\\]\\[info\\]\\[gc.*?\\] GC\\((\\d+)\\) .*? ([\\d\\.]+)(ms|s)"
    );

    private static final Pattern heapSummaryPattern = Pattern.compile(
            "Total heap: (\\d+)([KMG])->(\\d+)([KMG])\\((\\d+)([KMG])\\)"
    );
    private final CsvWriter csvWriter;
    //"*gc*.log"
    private final String filePattern;

    @SneakyThrows
    public G1GCReview(CsvWriter csvWriter,
                      @Value("${gc.file.pattern}") String filePattern) {

        this.csvWriter = csvWriter;
        this.filePattern = filePattern;

        this.csvWriter.writeHeader("GC","Pause (ms)","parent Folder","line");
    }

    private static double convertToMB(int value, String unit) {
        switch (unit) {
            case "K": return value / 1024.0;
            case "M": return value;
            case "G": return value * 1024.0;
            default: return value;
        }
    }



    public void reportLogs(File directory) {
        var files = IO.listFileRecursive(directory.getAbsolutePath(),filePattern);

        log.info("review files: {}",files);

        if(files == null)
            return;

        for (var file : files)
        {
            report(file);
        }
    }

    void report(File file)
    {
        int gcCount = 0;
        double totalPauseTimeMs = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {


               if(  line.contains("Pause Young (Normal)")&& line.matches(".*ms$"))
               {
                   //Non full stop
                   var pauseTime = Text.parseRE(line,"M\\) ","ms$");
                   csvWriter.appendRow("Pause Young (Normal)",pauseTime,file.getParentFile().getName(),line);
               }
               else if(line.contains("Pause Young (Concurrent Start)")&& line.matches(".*ms$"))
               {
                   //full stop
                   var pauseTime = Text.parseRE(line,"M\\) ","ms$");
                   csvWriter.appendRow("Pause Young (Concurrent Start)",pauseTime,file.getParentFile().getName(),line);
               }
               else if(line.contains("Pause Remark")&& line.matches(".*ms$"))
               {
                   //Normal young collections may occur. Marking finishes with two special stop-the-world pauses: Remark and Cleanup.
                       //stop-the-world pause
                   var pauseTime = Text.parseRE(line,"M\\) ","ms$");
                   csvWriter.appendRow("Pause Remark",pauseTime,file.getParentFile().getName(),line);
               }
               else if(line.contains("Pause Cleanup")&& line.matches(".*ms$"))
               {
                   //Normal young collections may occur. Marking finishes with two special stop-the-world pauses: Remark and Cleanup.
                   //stop-the-world pause
                   var pauseTime = Text.parseRE(line,"M\\) ","ms$");
                   csvWriter.appendRow("Pause Cleanup",pauseTime,file.getParentFile().getName(),line);
               }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
