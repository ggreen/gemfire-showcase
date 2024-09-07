package com.vmware.pivotal.labs.services.dataTx.geode.office;

import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.*;
import nyla.solutions.core.data.clock.Day;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YoungGenerationGcChartStatsVisitorTest {
    private ResourceInst resourceInst;
    private ResourceType resourceType;
    private Day dayFilter = Day.today();

    @BeforeEach
    void setUp() {
        resourceInst = mock(ResourceInst.class);
        resourceType = mock(ResourceType.class);
        when(resourceInst.getType()).thenReturn(resourceType);

    }

    @Test
    void isSkip_false_g1_young_generation() {

        YoungGenerationGcChartStatsVisitor subject = new YoungGenerationGcChartStatsVisitor(dayFilter);

        String expectedName = "G1YoungGeneration";
        String expectedTypeName = "VMGCStats";
        when(resourceInst.getName()).thenReturn(expectedName);
        when(resourceType.getName()).thenReturn(expectedTypeName);

        assertFalse(subject.isSkip(resourceInst));
    }

    @Test
    void isSkip_false_CMS_young_generation() {

        YoungGenerationGcChartStatsVisitor subject = new YoungGenerationGcChartStatsVisitor(dayFilter);

        String expectedName = "ParNew";
        String expectedTypeName = "VMGCStats";
        when(resourceInst.getName()).thenReturn(expectedName);
        when(resourceType.getName()).thenReturn(expectedTypeName);

        assertFalse(subject.isSkip(resourceInst));
    }

    @Test
    void isSkip_true_invalid_resource_instance() {

        YoungGenerationGcChartStatsVisitor subject = new YoungGenerationGcChartStatsVisitor(dayFilter);

        String expectedName = "invalid";
        String expectedTypeName = "VMGCStats";
        when(resourceInst.getName()).thenReturn(expectedName);
        when(resourceType.getName()).thenReturn(expectedTypeName);

        assertTrue(subject.isSkip(resourceInst));
    }

}