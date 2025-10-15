package showcase.gemfire.operations.stats;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.csv.CsvWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.gemfire.operations.stats.visitor.CsvClientStatsVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Test for the CSV visitor
 * @author gregory green
 */
@ExtendWith(MockitoExtension.class)
class CsvClientStatsVisitorTest {

    private GfStatsReader reader;
    private CsvClientStatsVisitor subject;
    private File file = Paths.get("src/test/resources/server1-2members.gfs").toFile();
    @Mock
    private CsvWriter csvWriter;
    private Day dayFilter = new Day("7/23/2025");

    @BeforeEach
    void setUp() throws IOException {

        reader = new GfStatsReader(file);
        subject = new CsvClientStatsVisitor(csvWriter,dayFilter);
    }

    @Test
    void visit() throws IOException {

        reader.acceptVisitors(subject);

        verify(csvWriter).writeHeader(any(String[].class));
    }
}