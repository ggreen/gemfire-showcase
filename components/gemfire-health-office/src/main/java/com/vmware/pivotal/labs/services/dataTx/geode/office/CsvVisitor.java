package com.vmware.pivotal.labs.services.dataTx.geode.office;

import com.vmware.data.services.apache.geode.operations.stats.GfStatsReader;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ArchiveInfo;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.util.Text;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Gregory Green
 */
public class CsvVisitor implements StatsVisitor
{
    private final CsvWriter csvWriter;
    private final Day dayFilter;

    private final String skipResourceInstNameRegExp = "RegionStats-managementRegionStats|org.apache.geode.*internal.*";

    private final String[] statNames;
    public CsvVisitor(CsvWriter csvWriter, Day dayFilter)
            throws IOException{
        this(csvWriter,dayFilter,
                "pagesPagedOut","cpuActive",
                "usedMemory",
                "ioWait",
                "collections",
                "collectionTime",
                "delayDuration",
                "functionExecutionsCompletedProcessingTime",
                "functionExecutionCalls",
                "functionExecutionsCompleted",
                "functionExecutionsExceptions",
                "entriesOnlyOnDisk",
                "getTime",
                "putTime",
                "updateTime",
                "puts",
                "creates",
                "updates",
                "entries",
                "gets",
                "events",
                "eventProcessingTime"
                );
    }

    public CsvVisitor(CsvWriter csvWriter, Day dayFilter, String... statNames)
    throws IOException
    {
        this.csvWriter = csvWriter;
        this.dayFilter = dayFilter;
        this.statNames = statNames;

        csvWriter.writeHeader("machine",  "type", "resource", "name",
                "time",
                "value");
    }


    public void visitResourceInst(ResourceInst resourceInst)
    {

        String machine =  "";
        String resourceInstName = resourceInst.getName();

        if(resourceInstName.matches(this.skipResourceInstNameRegExp))
            return;

        GfStatsReader archive = resourceInst.getArchive();

       if(archive != null)
       {
           ArchiveInfo archiveInfo = archive.getArchiveInfo();
           if(archiveInfo != null)
               machine = archiveInfo.getMachine();
       }

       String typeName = "";
        ResourceType resourceType = resourceInst.getType();
       if(resourceType != null)
           typeName = resourceType.getName();


        for (int i = 0; i < this.statNames.length; i++)
        {
            StatValue statValue = resourceInst.getStatValue(statNames[i]);
            if(statValue == null)
                continue;

            if(statValue.getSnapshotsMaximum() == 0)
                continue;

            double[] values = statValue.getSnapshots();
            long[] times = statValue.getRawAbsoluteTimeStamps();

            LocalDateTime startOfDay = Instant.ofEpochMilli(times[0])
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            LocalDateTime endOfDay = Instant.ofEpochMilli(times[times.length - 1])
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (dayFilter.isBefore(new Day(startOfDay.toLocalDate())))
            {
                System.out.println("Min timestamp date:" + Text.formatDate(new Date(times[0])) + " not matching day filter");
                return;
            }

            System.out.println("Max timestamp date:" + Text.formatDate(new Date(times[times.length - 1])));
            if (dayFilter.isAfter(new Day(endOfDay.toLocalDate())))
            {
                System.out.println("Max timestamp date:" + Text.formatDate(new Date(times[times.length - 1])) + " not matching day filter");
                return;
            }

            for (int j = 0; j < values.length; j++)
            {
                if(values[j] == 0)
                    continue;

                try
                {
                    if(!dayFilter.isSameDay(new Day(times[j])))
                        continue; //skip


                    csvWriter.appendRow(machine,
                            typeName,
                            resourceInstName,
                            statValue
                            .getDescriptor().getName(),
                            formatTime(times[j]),
                            String.valueOf(values[j]));
                }
                catch (IOException e)
                {
                    throw new RuntimeException("ERROR"+e.getMessage()+" stat:"+statValue,e);
                }
            }
        }
    }

    protected String formatTime(long time)
    {
        return Text.formatDate("yyyyMMddHHmmss",new Date(time));
    }
}
