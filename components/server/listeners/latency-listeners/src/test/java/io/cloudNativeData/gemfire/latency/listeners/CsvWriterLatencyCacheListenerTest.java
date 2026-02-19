package io.cloudNativeData.gemfire.latency.listeners;

import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvWriterLatencyCacheListenerTest {

    private CsvWriterLatencyCacheListener subject;
    @Mock
    private EntryEvent<String, PdxInstance> event;
    @Mock
    private CsvWriter writer;
    @Mock
    private PdxInstance metrics;

    @Mock
    private Cache cache;

    private final String filePath = IO.tempDir()+"/latency.csv";
    private final Long startTime = System.currentTimeMillis();


    @BeforeEach
    void setUp() {
        subject = new CsvWriterLatencyCacheListener(() -> writer);
    }

    @Test
    void given_event_when_after_write_then_write_csv() throws IOException {

        when(event.getNewValue()).thenReturn(metrics);
        when(event.getKey()).thenReturn("key");
        when(metrics.getField(anyString())).thenReturn(startTime);
        subject.afterCreate(event);

        verify(writer).appendRow(
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }

    @Test
    void given_event_when_after_update_then_write_csv() throws IOException {

        when(event.getNewValue()).thenReturn(metrics);
        when(event.getKey()).thenReturn("key");
        when(metrics.getField(anyString())).thenReturn(startTime);
        subject.afterUpdate(event);

        verify(writer).appendRow(
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }

    @Test
    void given_properties_when_int_then_get_supplier_using_file_properties() throws IOException {

        IO.dir().delete(Paths.get(filePath).toFile());

        subject  = new CsvWriterLatencyCacheListener();
        var properties = new Properties();
        properties.setProperty(CsvWriterLatencyCacheListener.FILE_PATH_PROP_NM,filePath);
        subject.initialize(cache, properties);

        when(event.getNewValue()).thenReturn(metrics);
        when(event.getKey()).thenReturn("key");
        when(metrics.getField(anyString())).thenReturn(startTime);


        subject.afterCreate(event);

        assertTrue(IO.exists(Paths.get(filePath).toFile().getAbsolutePath()));
    }

    @Test
    void given_no_file_path_arg_when_afterCreate_then_use_current_dir() throws IOException {
        IO.dir().delete(Paths.get(CsvWriterLatencyCacheListener.DEFAULT_FILE_PATH).toFile());

        subject  = new CsvWriterLatencyCacheListener();

        when(event.getNewValue()).thenReturn(metrics);
        when(event.getKey()).thenReturn("key");
        when(metrics.getField(anyString())).thenReturn(startTime);

        subject.afterCreate(event);

        assertTrue(IO.exists(Paths.get(CsvWriterLatencyCacheListener.DEFAULT_FILE_PATH).toFile().getAbsolutePath()));
        //clean up
        IO.dir().delete(Paths.get(CsvWriterLatencyCacheListener.DEFAULT_FILE_PATH).toFile());
        assertFalse(IO.exists(Paths.get(CsvWriterLatencyCacheListener.DEFAULT_FILE_PATH).toFile().getAbsolutePath()));
    }
}