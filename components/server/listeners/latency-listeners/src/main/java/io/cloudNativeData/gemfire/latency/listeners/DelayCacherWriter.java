package io.cloudNativeData.gemfire.latency.listeners;

import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheWriterAdapter;

import static java.lang.Thread.sleep;

public class DelayCacherWriter extends CacheWriterAdapter<Object,Object> {
    private final static long SLEEP_TIME_MS = 1000*30;

    @Override
    public void beforeCreate(EntryEvent<Object, Object> event) throws CacheWriterException {
        try {
            sleep(SLEEP_TIME_MS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeUpdate(EntryEvent<Object, Object> event) throws CacheWriterException {
        beforeCreate(event);
    }
}
