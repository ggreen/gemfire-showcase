package showcase.gemfire.operations.stats.visitor;
import com.vmware.data.services.gemfire.operations.stats.statInfo.ArchiveInfo;
import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.gemfire.operations.stats.visitors.StatsVisitor;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.util.Text;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static nyla.solutions.core.util.Organizer.toMap;

/**
 * @author Gregory Green
 */
public class CsvVisitor implements StatsVisitor
{
    private final CsvWriter csvWriter;
    private final Day dayFilter;

    private final String skipResourceInstNameRegExp = "RegionStats-managementRegionStats|org.apache.geode.*internal.*";

    private final Map<String, List<String>> statNamesMap;
    public CsvVisitor(CsvWriter csvWriter, Day dayFilter)
            throws IOException{
        this(csvWriter,dayFilter,
                toMap(
                        "VMStats", List.of("fdsOpen","fdLimit","processCpuTime","threads"),
                        "DistributionStats", List.of(
                                "nodes",
                                "functionExecutionThreads",
                                "functionExecutionQueueSize",
                                "highPriorityThreads",
                                "highPriorityQueueSize",
                                "partitionedRegionThreads",
                                "partitionedRegionQueueSize"),
                        "Stats", List.of("collections","collectionTime"),
                        "StatSampler",List.of("delayDuration","jvmPauses"),
                        "VMMemoryPoolStats",List.of("currentUsedMemory","currentMaxMemory"),
                        "LinuxSystemStats", List.of("cachedMemory",
                                "cpuActive",
                                "freeMemory",
                                "physicalMemory",
                                "recvBytes",
                                "recvDrops",
                                "xmitBytes",
                                "xmitDrops"),
                        "PartitionedRegionStats",List.of("bucketCount","primaryBucketCount","dataStoreBytesInUse"),
                        "CachePerfStats",List.of("cacheListenerCallsInProgress","cacheWriterCallsInProgress","loadsInProgress","dataStoreEntryCount"),
                        "ResourceManagerStats",List.of("heapCriticalEvents","evictionStartEvents"),
                        "CacheServerStats", List.of(
                                "currentClients",
                                "currentClientConnections",
                                "closeConnectionRequests",
                                "connectionsTimedOut",
                                "threadQueueSize")
                ));
    }

    public CsvVisitor(CsvWriter csvWriter, Day dayFilter, Map<String, List<String>> statNamesMap)
            throws IOException
    {
        this.csvWriter = csvWriter;
        this.dayFilter = dayFilter;
        this.statNamesMap = statNamesMap;

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

        var archive = resourceInst.getArchive();

        if(archive != null)
        {
            ArchiveInfo archiveInfo = archive.getArchiveInfo();
            if(archiveInfo != null)
                machine = archiveInfo.getMachine();
        }

        var typeName = "";
        var resourceType = resourceInst.getType();
        if(resourceType != null)
            typeName = resourceType.getName();


        var statNames = statNamesMap.get(typeName);
        if(statNames ==null || statNames.isEmpty()){

            System.out.println("No stats configures for type:"+typeName);
            return;
        }

        for (String statName : statNames)
        {
            var statValue = resourceInst.getStatValue(statName);
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