package com.vmware.pivotal.labs.services.dataTx.geode.office;


import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatsToChartTest
{
	@BeforeAll
	public static void setup()
	{
		//DAY_FILTER
		System.setProperty("DAY_FILTER", "4/25/2018");
		
	}

	@Test
	public void testConvert()
	throws Exception
	{
		Day day = new Day("7/4/2019");
		System.out.println(IO.delete(new File("target/graph.png")));
		StatsToChart subject = new StatsToChart(new CpuAboveThresholdChartStatsVisitor(day));
		File file = new File("src/test/resources/stats");
		//File file = new File("/Projects/LifeSciences/Humana/docs/Vantage/docs/assessments/performance/perf-test-results/april-23-2018/tmp/support");
		
		
		Chart chart = subject.convert(file);
		
		assertNotNull(chart);
		
		
		byte [] bytes = chart.getBytes();
		
		assertTrue(bytes != null  && bytes.length > 0);
		
		IO.writeFile("target/graph.png", bytes);
	}

}
