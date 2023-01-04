package com.vmware.pivotal.labs.services.dataTx.geode.office;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatDescriptor;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import nyla.solutions.core.data.Property;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.util.Text;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 *  
 *  The class will generate a bar graph of all ParNew collection times greater 
 *  than a given millisecond threshold.
 *  
 * </pre>
 * @author Gregory Green
 *
 */
public class ParNewCollectionTimeThresholdChartStatsVisitor
		extends AbstractChartVisitor
{
	private String appName = null;
	private String resourceResourceNameFilter = "ParNew|.*Young.*";
	private String filterTypeName = "VMGCStats".toUpperCase();
	private String filterStatName = "collectionTime";
	private Map<Property, Double> maxMap = new HashMap<>();
	private final Double threshold;

	private final Day dayFilter;

	public ParNewCollectionTimeThresholdChartStatsVisitor(Day dayFilter)
	{
		this(dayFilter, 50.1);
	}//---------------------------------------------s
	public ParNewCollectionTimeThresholdChartStatsVisitor(Day dayFilter, double threshold)
	{
		this.threshold = Double.valueOf(threshold);
		this.dayFilter = dayFilter;
		this.chart.setTitle("Max Par New collection time per second on "+this.dayFilter);
		this.chart.setGraphType(Chart.BAR_GRAPH_TYPE);
	}

//	@Override
	public void visitResourceInsts(ResourceInst[] resourceInsts)
	{
		this.appName = StatsUtil.getAppName(resourceInsts);
	}

//	@Override
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

			if(statValue.getSnapshotsMaximum() < threshold)
				return;
			

			StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);

			long [] times = statValue.getRawAbsoluteTimeStamps();
			double [] values = statValue.getSnapshots();
			
			
			String timeFormat = "HH:mm:ss";


			for (int i = 0; i < values.length; i++) {

				Date date = new Date(times[i]);

				Day day = new Day(date);
				if (!dayFilter.isSameDay(day))
					continue;

				String timeValueText = Text.formatDate(timeFormat, date);
				Property timeValue = new Property(this.appName, timeValueText);

				//get previous Max
				Double max = this.maxMap.get(timeValue);

				if (max == null)
					max = Double.valueOf(values[i]);
				else
					max = Double.valueOf(Math.max(values[i], max.doubleValue()));

				if (max.doubleValue() > statValue.getSnapshotsMaximum())
					throw new IllegalArgumentException(max.doubleValue() + ">" + statValue.getSnapshotsMaximum() + " statValue:" + statValue);

				if (values[i] >= threshold)
					this.maxMap.put(timeValue, max);

			}

		}
		
		for (Map.Entry<Property, Double> entry : maxMap.entrySet())
		{
			if(entry.getValue() == null || entry.getKey() == null ||
					entry.getKey().getName() == null ||
			entry.getKey().getValue() == null)
				continue;

			this.chart.plotValue(entry.getValue(), 
			entry.getKey().getName(),
			entry.getKey().getValue().toString());
		}
		
	}//------------------------------------------------

	protected boolean isSkip(ResourceInst resourceInst) {
		String name = resourceInst.getName();

		ResourceType resourceType= resourceInst.getType();
		return !name.matches(this.resourceResourceNameFilter) ||  resourceType == null || resourceType.getName() == null ||
			(this.filterTypeName != null && !resourceType.getName().toUpperCase().contains(this.filterTypeName));
	}


}
