package com.vmware.data.services.apache.geode.operations.stats.visitors;


import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import com.vmware.data.services.apache.geode.operations.stats.GfStatsReader;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatDescriptor;
import nyla.solutions.core.io.csv.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extracts details about regions
 *
 * @author Gregory Green
 */
public class CsvStatsVisitor implements StatsVisitor
{
	private static final String NAME_PART_SEPARATOR = "-";
    private final String typeName;
    private String[] statNames = null;
    private final File csvDirOrFile;

    public CsvStatsVisitor(File csvDirOrFile)
    {
        this(csvDirOrFile, null);
    }

    public CsvStatsVisitor(File csvDirOrFile, String typeName, String... statNames)
    {


        if (typeName == null || typeName.length() == 0)
            this.typeName = null;
        else
            this.typeName = typeName.toUpperCase();

        this.statNames = statNames;

        this.csvDirOrFile = csvDirOrFile;

    }//------------------------------------------------

    @Override
    public void visitResourceInst(ResourceInst resourceInst)
    {
        String name = resourceInst.getName();

        ResourceType resourceType = resourceInst.getType();

        boolean skip = resourceType == null || resourceType.getName() == null ||
                (this.typeName != null && !resourceType.getName().toUpperCase().contains(this.typeName));

        if (skip)
        {
            System.out.println("skipping resourceType:" + resourceType + " name:" + name);
            return;
        }

        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> headers = new ArrayList<String>();

        headers.add("name");
        values.add(name);


        StatValue[] statValues = resourceInst.getStatValues();
        if (statValues == null)
            return;

        for (StatValue statValue : statValues)
        {
            String statName = statValue.getDescriptor().getName();

            if (this.statNames != null && this.statNames.length > 0)
            {
                if (Arrays.binarySearch(statNames, statName) < 0)
                    continue; //skip
            }
            StatValue dataStoreEntryCount = resourceInst.getStatValue(statName);

            StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);

            headers.add(statName + "        " + statDescriptor.getDescription());

            values.add(String.valueOf(dataStoreEntryCount.getSnapshotsMaximum()));
        }

        writeCsv(resourceInst, headers, values);

    }//------------------------------------------------


    void writeCsv(ResourceInst resourceInst, List<String> headers, List<String> values)
    {
        File file = toFile(csvDirOrFile, resourceInst);
        CsvWriter csvWriter = new CsvWriter(file);

        try
        {
            csvWriter.writeHeader(headers);
            csvWriter.appendRow(values);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }//------------------------------------------------


    protected File toFile(File csvFile, ResourceInst resourceInst)
    {

        if (csvFile.isDirectory())
        {
            StringBuilder fileName = new StringBuilder();
			fileName.append(csvFile.getAbsolutePath())
                    .append("/");

            constructNameWithResourceInst(fileName,resourceInst);

            fileName.append(".csv");

            return Paths.get(fileName.toString()).toFile();
        }
        else if(csvFile.exists() && !csvFile.getName().endsWith(".csv"))
        {
            StringBuilder fileName = new StringBuilder();
            constructNameWithResourceInst(fileName,resourceInst);

            return Paths.get(
                    csvFile.getAbsolutePath().concat(".csv"))
                    .toFile();
        }

        return csvFile;

    }

    private void constructNameWithResourceInst(StringBuilder fileName,ResourceInst resourceInst)
    {
        if(resourceInst == null)
            return;

        GfStatsReader archive = resourceInst.getArchive();
        if (archive != null)
            fileName.append(archive.getArchiveFile().getName()).append(NAME_PART_SEPARATOR);


        fileName.append(resourceInst.getName());

        ResourceType resourceType = resourceInst.getType();
        if (resourceType != null)
            fileName.append(NAME_PART_SEPARATOR).append(resourceType.getName());
    }
}
