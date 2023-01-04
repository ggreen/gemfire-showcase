package com.vmware.pivotal.labs.services.dataTx.geode.office;

import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;
import nyla.solutions.office.chart.Chart;

public interface ChartStatsVisitor extends StatsVisitor
{

	/**
	 * @return the chart
	 */
	Chart getChart();
	

}