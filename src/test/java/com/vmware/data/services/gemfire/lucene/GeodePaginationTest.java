package com.vmware.data.services.gemfire.lucene;

import nyla.solutions.core.data.MapEntry;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GeodePaginationTest
{
    private Region<String, Collection<?>> pageRegion;
    private GeodePagination subject;

    @BeforeEach
    void setUp()
    {
        pageRegion = mock(Region.class);
        subject = new GeodePagination();
    }

    @Test
    void readKeys()
    {
        String id = "id";
        int pageNumber = 3;
        Collection<String> keys = Arrays.asList("hello");
        when(pageRegion.get(anyString())).thenReturn((Collection)keys);

        Collection<String> actual = subject.readKeys(id,pageNumber,pageRegion);
        assertNotNull(actual);
        verify(pageRegion).get(anyString());
    }

    @Test
    void storePaginationMap()
    {
        String id = "id";
        int pageSize = 3;
        Region<String, Collection<String>> pageKeysRegion = mock(Region.class);
        Map<Object, Object> map = new HashMap<>();
        Map.Entry<String, String>  mapEntry = mock(Map.Entry.class);
        List<Map.Entry<String, String>> results = Arrays.asList(mapEntry);

        subject.storePaginationMap(id,pageSize,pageKeysRegion,results);

        verify(pageKeysRegion).put(anyString(), any());
    }

    @Test
    void toPageKey()
    {
        String id = "id";
        int pageNumber = 3;
        assertEquals(id+"-"+pageNumber,GeodePagination.toPageKey(id,pageNumber));
    }

    @Test
    void toKeyPages()
    {

        int pageSize = 2;
        String key = "1";
        String value = "1";
        Map.Entry<String, String> entry = new MapEntry<>(key,value);
        List<Map.Entry<String, String>> mapEntries = Arrays.asList(entry,entry,entry,entry,entry);
        List<Collection<String>> actual = GeodePagination.toKeyPages(mapEntries, pageSize);

        assertNotNull(actual);
        assertEquals(3,actual.size());

        assertEquals(pageSize,actual.iterator().next().size());

    }

    @Test
    void storePagination()
    {
    }

    @Test
    void readResultsByPage()
    {
    }

    @Test
    void readResultsByPageValues()
    {
    }

    @Test
    void clearSearchResultsByPage()
    {
    }
}