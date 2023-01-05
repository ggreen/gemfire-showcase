package com.vmware.pivotal.labs.services.dataTx.geode.office;

import java.io.File;

import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.gemfire.operations.stats.statInfo.ResourceType;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParNewCollectionTimeThresholdChartStatsVisitorTest
{
	ResourceInst resourceInst;
	ResourceType resourceType;
	Day dayFilter = Day.today();

	@BeforeEach
	void setUp() {
		resourceInst = mock(ResourceInst.class);
		resourceType = mock(ResourceType.class);
		when(resourceInst.getType()).thenReturn(resourceType);

	}

	@org.junit.jupiter.api.Test
	void isSkip_false_g1_young_generation() {

		ParNewCollectionTimeThresholdChartStatsVisitor subject = new ParNewCollectionTimeThresholdChartStatsVisitor(dayFilter);

		String expectedName = "G1YoungGeneration";
		String expectedTypeName = "VMGCStats";
		when(resourceInst.getName()).thenReturn(expectedName);
		when(resourceType.getName()).thenReturn(expectedTypeName);

		assertFalse(subject.isSkip(resourceInst));
	}

	@org.junit.jupiter.api.Test
	void isSkip_false_CMS_young_generation() {

		ParNewCollectionTimeThresholdChartStatsVisitor subject = new ParNewCollectionTimeThresholdChartStatsVisitor(dayFilter);

		String expectedName = "ParNew";
		String expectedTypeName = "VMGCStats";
		when(resourceInst.getName()).thenReturn(expectedName);
		when(resourceType.getName()).thenReturn(expectedTypeName);

		assertFalse(subject.isSkip(resourceInst));
	}

	@org.junit.jupiter.api.Test
	void isSkip_true_invalid_resource_instance() {

		ParNewCollectionTimeThresholdChartStatsVisitor subject = new ParNewCollectionTimeThresholdChartStatsVisitor(dayFilter);

		String expectedName = "invalid";
		String expectedTypeName = "VMGCStats";
		when(resourceInst.getName()).thenReturn(expectedName);
		when(resourceType.getName()).thenReturn(expectedTypeName);

		assertTrue(subject.isSkip(resourceInst));
	}
	@Test
	public void testGetChart()
	throws Exception
	{
		Day day = new Day("7/4/2019");
		ParNewCollectionTimeThresholdChartStatsVisitor v = new ParNewCollectionTimeThresholdChartStatsVisitor(day);
		
		StatsToChart c = new StatsToChart(v);
		
		File f =  null;
		//f = new File("/Projects/LifeSciences/Humana/docs/Vantage/docs/assessments/performance/perf-test-results/may-16-2018-noFunctionChange/server");
		f = new File("src/test/resources/stats");
		//f = new File("/Projects/LifeSciences/Humana/docs/Vantage/docs/assessments/performance/perf-test-results/may-16-2018-noFunctionChange/server/tmp3/vantage-louweblqs159-server1/stats.gfs");
		Chart chart = c.convert(f);
		
    	IO.writeFile("target/NewPar.png", chart.getBytes());
	}

}
