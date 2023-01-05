package com.vmware.pivotal.labs.services.dataTx.geode.office;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;
import com.vmware.data.services.gemfire.operations.stats.statInfo.*;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.csv.CsvWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CsvVisitorTest
{
    Day dayFilter = Day.today();
    CsvWriter mockWriter;
    CsvVisitor subject;
    StatValue statValue;
     ResourceInst resourceInst;
    GfStatsReader statReader;
    ArchiveInfo archiveInfo;
    ResourceType resourceType;
    StatDescriptor statDescription;
    String expectedStatName = "expectStatName";
    private String expectedResourceInstName = "resourceName";
    private final String skipResourceInstRegExp = "RegionStats-managementRegionStats";


    @BeforeEach
    public void setUp()
    throws IOException
    {
        resourceType = mock(ResourceType.class);
        mockWriter = mock(CsvWriter.class);
        String[] statNames = {expectedStatName};
        subject = new CsvVisitor(mockWriter,dayFilter,statNames);
        statValue = mock(StatValue.class);
        resourceInst = mock(ResourceInst.class);
        statReader = mock(GfStatsReader.class);
        archiveInfo = mock(ArchiveInfo.class);
        statDescription = mock(StatDescriptor.class);

        when(statReader.getArchiveInfo()).thenReturn(archiveInfo);
        when(resourceInst.getName()).thenReturn(expectedResourceInstName);
        when(resourceInst.getArchive()).thenReturn(statReader);
        when(resourceInst.getStatValue(anyString())).thenReturn(statValue);

        when(resourceInst.getType()).thenReturn(resourceType);
        when(statValue.getType()).thenReturn(resourceType);

        when(statValue.getDescriptor()).thenReturn(statDescription);
        StatValue[] statValues = {statValue};
        when(resourceInst.getStatValues()).thenReturn(statValues);

    }

    @Test
    void visitResourceInst_skipRegionStats_managementRegionStats()
    throws IOException
    {
        long[] rawAbsoluteTimestamp = { System.currentTimeMillis()};
        double [] values = {2.1};
        String expectedMachineName = "machine";

        when(archiveInfo.getMachine()).thenReturn(expectedMachineName);
        when(statValue.getRawAbsoluteTimeStamps()).thenReturn(rawAbsoluteTimestamp);

        when(statValue.getSnapshots()).thenReturn(values);
        when(statValue.getSnapshotsMaximum()).thenReturn(values[0]);

        when(statDescription.getName()).thenReturn(expectedStatName);

        String expectedName = "expectedResourceTypeName";
        when(resourceType.getName()).thenReturn(expectedName);


        when(resourceInst.getName()).thenReturn("RegionStats-managementRegionStats");
        subject.visitResourceInst(resourceInst);

        verify(mockWriter,never()).appendRow(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }
    @Test
    void visitResourceInst_skipRegionStats_geode_internal()
            throws IOException
    {
        long[] rawAbsoluteTimestamp = { System.currentTimeMillis()};
        double [] values = {2.1};
        String expectedMachineName = "machine";

        when(archiveInfo.getMachine()).thenReturn(expectedMachineName);
        when(statValue.getRawAbsoluteTimeStamps()).thenReturn(rawAbsoluteTimestamp);

        when(statValue.getSnapshots()).thenReturn(values);
        when(statValue.getSnapshotsMaximum()).thenReturn(values[0]);

        when(statDescription.getName()).thenReturn(expectedStatName);

        String expectedName = "expectedResourceTypeName";
        when(resourceType.getName()).thenReturn(expectedName);


        when(resourceInst.getName()).thenReturn("org.apache.geode.internal.fdd");
        subject.visitResourceInst(resourceInst);

        verify(mockWriter,never()).appendRow(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }

    @Test
    void visitResourceInst()
            throws IOException
    {
        long[] rawAbsoluteTimestamp = { System.currentTimeMillis()};
        double [] values = {2.1};
        String expectedMachineName = "machine";

        when(archiveInfo.getMachine()).thenReturn(expectedMachineName);
        when(statValue.getRawAbsoluteTimeStamps()).thenReturn(rawAbsoluteTimestamp);

        when(statValue.getSnapshots()).thenReturn(values);
        when(statValue.getSnapshotsMaximum()).thenReturn(values[0]);

        when(statDescription.getName()).thenReturn(expectedStatName);

        String expectedName = "expectedResourceTypeName";
        when(resourceType.getName()).thenReturn(expectedName);


        when(resourceInst.getName()).thenReturn(this.expectedResourceInstName);
        subject.visitResourceInst(resourceInst);


        verify(resourceInst,atLeast(1)).getStatValue(anyString());
        verify(mockWriter).writeHeader(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );

        verify(mockWriter).appendRow(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString());
        verify(resourceInst).getType();
        verify(resourceType).getName();
        verify(archiveInfo).getMachine();

    }
    @Test
    void visitResourceInst_skip_flatline()
    throws IOException
    {
        long[] rawAbsoluteTimestamp = { System.currentTimeMillis()};
        double [] values = {0};
        String expectedMachineName = "machine";

        when(archiveInfo.getMachine()).thenReturn(expectedMachineName);
        when(statValue.getRawAbsoluteTimeStamps()).thenReturn(rawAbsoluteTimestamp);

        when(statValue.getSnapshots()).thenReturn(values);
        String expectedStatName = "expectStatName";
        when(statDescription.getName()).thenReturn(expectedStatName);

        String expectedName = "expectedResourceTypeName";
        when(resourceType.getName()).thenReturn(expectedName);

        subject.visitResourceInst(resourceInst);

        verify(mockWriter,never()).appendRow(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString());
    }

    @Test
    void formatTime()
    {

        LocalDateTime now = LocalDateTime.now();
        long expectTime = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String expected = now.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));

        String actual = subject.formatTime(expectTime);
        assertEquals(expected,actual);

    }
}