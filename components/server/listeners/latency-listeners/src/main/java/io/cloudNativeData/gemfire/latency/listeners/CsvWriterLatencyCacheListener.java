package io.cloudNativeData.gemfire.latency.listeners;

import nyla.solutions.core.io.csv.CsvWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Supplier;

import static java.lang.String.valueOf;

/**
 * Writes latency metric to a CSV.
 * @author Gregory Green
 */
public class CsvWriterLatencyCacheListener extends CacheListenerAdapter<String, PdxInstance>
        implements  Declarable {
    static final String FILE_PATH_PROP_NM = "latency-csv-file-path";
    static final String DEFAULT_FILE_PATH = "./latency_gateway.csv";
    private final Logger logger = LogManager.getLogger(this.getClass());


    private final Supplier<CsvWriter> supplier;
    private CsvWriter csvWriter;
    private final Properties properties = new Properties();

    public CsvWriterLatencyCacheListener(Supplier<CsvWriter> supplier) {
        this.supplier = supplier;
    }

    public CsvWriterLatencyCacheListener() {
        this.supplier = () -> {
            return new CsvWriter(Paths.get(properties.getProperty(FILE_PATH_PROP_NM,DEFAULT_FILE_PATH)).toFile());
        };
    }

    @Override
    public void afterCreate(EntryEvent<String, PdxInstance> event) {
        processEvent(event);
    }

    @Override
    public void afterUpdate(EntryEvent<String, PdxInstance> event) {
        processEvent(event);
    }

    private void processEvent(EntryEvent<String, PdxInstance> event) {

        var endTime = System.currentTimeMillis();

        if(this.csvWriter == null)
            this.csvWriter = supplier.get();

        var startTime = (Long)event.getNewValue().getField("startTime");

        if(startTime == null)
        {
            logger.warn("Field startTime not found. CSV record cannot be captured");
            return;
        }

        var latency = endTime - startTime;

        csvWriter.appendRow(event.getKey(),valueOf(latency),valueOf(startTime),valueOf(endTime));

    }

    /**
     *
     * @param cache the cache
     * @param properties the properties
     */
    @Override
    public void initialize(Cache cache, Properties properties) {
        this.properties.setProperty(FILE_PATH_PROP_NM, properties.getProperty(FILE_PATH_PROP_NM,
                DEFAULT_FILE_PATH));
    }
}
