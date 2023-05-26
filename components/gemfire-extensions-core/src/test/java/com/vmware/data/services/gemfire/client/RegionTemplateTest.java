package com.vmware.data.services.gemfire.client;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.security.user.data.UserProfile;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static nyla.solutions.core.util.Organizer.toMap;
import static nyla.solutions.core.util.Organizer.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionTemplateTest {

    @Mock
    private Region<String, UserProfile> region;

    private RegionTemplate subject;
    private UserProfile expected = JavaBeanGeneratorCreator.of(UserProfile.class).create();
    private Object callBackArgs = "args";
    private String key = "imani";
    private Set<String> expectedKeySet = toSet(key);
    private Map<String, UserProfile> expectedMap = toMap(key,expected);


    @BeforeEach
    void setUp() {
        subject = new RegionTemplate(region);
    }

    @Test
    void get() {
        when(region.get(anyString())).thenReturn(expected);

        assertEquals(expected, subject.get(key));
    }

    @Test
    void testGet() {
        when(region.get(anyString(),any())).thenReturn(expected);

        assertEquals(expected, subject.get(key,callBackArgs));
    }

    @Test
    void put() {

        subject.put(key,expected);

        verify(region).put(anyString(),any(UserProfile.class));
    }

    @Test
    void testPut() {
        subject.put(key,expected,callBackArgs);

        verify(region).put(anyString(),any(UserProfile.class),any());
    }

    @Test
    void keySet() {

        when(region.keySetOnServer()).thenReturn(expectedKeySet);

        assertEquals(expectedKeySet, subject.keySet());
    }



    @Test
    void keySetOnServer() {
        when(region.keySetOnServer()).thenReturn(expectedKeySet);

        assertEquals(expectedKeySet, subject.keySetOnServer());

    }

    @Test
    void putAll() {
        subject.putAll(expectedMap);

        verify(region).putAll(any(Map.class));
    }

    @Test
    void testPutAll() {

        subject.putAll(expectedMap,callBackArgs);

        verify(region).putAll(any(Map.class),any());
    }

    @Test
    void removeAll() {
        subject.removeAll(expectedKeySet);

        verify(region).removeAll(any());
    }

    @Test
    void testRemoveAll() {

        subject.removeAll(expectedKeySet,callBackArgs);

        verify(region).removeAll(any(),any());
    }

    @Test
    void getAll() {
        when(region.getAll(any())).thenReturn(expectedMap);

        assertEquals(expectedMap,subject.getAll(expectedKeySet));
    }

    @Test
    void getAllValues() {

        when(region.getAll(any(),any())).thenReturn(expectedMap);

        assertEquals(expectedMap,subject.getAll(expectedKeySet,callBackArgs));
    }


    @Test
    void remove() {
        subject.remove(key);

        verify(region).remove(any());
    }


    @Test
    void putIfAbsent() {
        subject.putIfAbsent(key,expected);

        verify(region).putIfAbsent(any(),any());
    }

    @Test
    void testRemove() {

        subject.remove(key,callBackArgs);

        verify(region).remove(any(),any());
    }

    @Test
    void getRegion() {
    }
}