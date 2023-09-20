package com.vmware.pivotal.labs.services.dataTx.geode.office;


import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.gemfire.operations.stats.statInfo.StatDescriptor;
import com.vmware.data.services.gemfire.operations.stats.statInfo.StatValue;
import nyla.solutions.core.data.NumberedProperty;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.util.Text;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 * Generates a chart for Garbage collection Par New Collections per hour.
 * </pre>
 * @author Gregory Green
 *
 */
public class YoungGenerationGcChartStatsVisitor extends AbstractChartVisitor
{
	private  Map<String,NumberedProperty> countPerHour = new TreeMap<>();
	private String resourceResourceNameFilter = "ParNew|.*Young.*";
	private String filterTypeName = "VMGCStats".toUpperCase();
	private String filterStatName = "collections";
	private String appName;
	private  int threshold = 1;
	private final Day dayFilter;

	public YoungGenerationGcChartStatsVisitor(Day dayFilter)
	{
		this.dayFilter = dayFilter;
		String title = "Collections per second greater than "+threshold+" "+
		Text.formatDate("MM/dd/yyyy",dayFilter.getDate());
		
		this.chart.setTitle(title);
		this.chart.setGraphType(Chart.BAR_GRAPH_TYPE);
	}//------------------------------------------------


	@Override
	public void visitResourceInsts(ResourceInst[] resourceInsts)
	{
		this.appName = StatsUtil.getAppName(resourceInsts);
	}
	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{

		


		boolean skip = isSkip(resourceInst);

		if(skip)
		{
			return;
		}

		
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
		for (StatValue statValue : statValues)
		{
			String statName = statValue.getDescriptor().getName();
			
			if(filterStatName != null && !filterStatName.equalsIgnoreCase(statName))
			{
				continue;  //skip;
			}
			

			StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);

			long [] times = statValue.getRawAbsoluteTimeStamps();
			//double [] values = statValue.getRawSnapshots();
			double [] values = statValue.getSnapshots();
			
			
			String timeFormat = "HH:mm:ss";
			
			NumberedProperty current = null;
			Date date  = null;
			Day day = null;
			int newValue;
			
			for (int i = 0; i < values.length; i++)
			{
				
			    date = new Date(times[i]);
				day = new Day(date);
				
				if(!this.dayFilter.isSameDay(day))
					continue;
					
				String timeValue = Text.formatDate(timeFormat,date);
				
				
				NumberedProperty value = this.countPerHour.get(timeValue);
				
				 current = new NumberedProperty(appName,Integer.valueOf(Double.valueOf(values[i]).intValue()));
				
				if(value == null)
					value = current;
				else
				{
					newValue = Integer.valueOf(value.getNumber() + current.getNumber());
					value.setNumber(newValue);
				}
				countPerHour.put(timeValue, value);						
			}

		}
		
		String entryName = null;
		Integer intValue;
		for(Map.Entry<String, NumberedProperty> entry : this.countPerHour.entrySet())
		{
			entryName = entry.getValue().getName();
			if(entryName == null)
				entryName = "Unknown";
			
			entryName = entryName.replace("amd64 ", "");

			NumberedProperty value = entry.getValue();

			if(value == null)
				continue;

			intValue = value.getValueInteger();
			if(intValue == null)
				continue;
			
			if(intValue >= threshold)
			{
				this.chart.plotValue(intValue, entryName, entry.getKey());
			}
		}
		
		
	}//------------------------------------------------

	protected boolean isSkip(ResourceInst resourceInst) {
		String name = resourceInst.getName();
		ResourceType resourceType = resourceInst.getType();

		return !name.matches(resourceResourceNameFilter) ||  resourceType == null || resourceType.getName() == null ||
			(this.filterTypeName != null && !resourceType.getName().toUpperCase().contains(this.filterTypeName));
	}

}
