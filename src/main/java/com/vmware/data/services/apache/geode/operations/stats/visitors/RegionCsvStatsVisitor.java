package com.vmware.data.services.apache.geode.operations.stats.visitors;




import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.util.Config;

/**
 * Extracts details about regions
 * @author Gregory Green
 *
 */
public class RegionCsvStatsVisitor implements StatsVisitor
{
	private static final String [] defaultStateNames = {"dataStoreEntryCount",
			"dataStoreBytesInUse",
			"lowRedundancyBucketCount",
			"localMaxMemory"};
	
	private final CsvWriter csvWriter;
	private final String[] statNames;
	
	/**
	 * 
	 * @param file the STAT file
	 */
	public RegionCsvStatsVisitor(File file)
	{
		this(file,null);
	}//------------------------------------------------
	public RegionCsvStatsVisitor(File file,String[] statNames)
	{
		if(statNames !=null)
		{
			this.statNames = statNames;
		}
		else
		{
			this.statNames = Config.getPropertyStrings(RegionCsvStatsVisitor.class,"statNames",defaultStateNames);
		}
		csvWriter = new CsvWriter(file);
	}

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		
		String name = resourceInst.getName();
		
		if(!resourceInst.getType().isRegion())
			return;
		
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> headers = new ArrayList<String>();
		
		headers.add("machine");
		headers.add("region");

		values.add(resourceInst.getArchive().getArchiveInfo().getMachine());
		values.add(name);
		
		
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
		/*
		 * dataStoreEntryCount
		 * dataStoreBytesInUse
		 * lowRedundancyBucketCount
		 * configuredRedundantCopies
		 * actualRedundantCopies
		 * localMaxMemory
		 */

		
		
		
		for (String statName : statNames)
		{
			//String statName = statValue.getDescriptor().getName();
			
			StatValue dataStoreEntryCount = resourceInst.getStatValue(statName);

			
			headers.add(statName+"        "+resourceInst.getType().getStat(statName).getDescription());
			
			values.add(String.valueOf(dataStoreEntryCount.getSnapshotsMaximum()));
		}
		
		try
		{
			csvWriter.writeHeader(headers);
			csvWriter.appendRow(values);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
