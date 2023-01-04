package com.vmware.pivotal.labs.services.dataTx.geode.office;

import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * JvmAvgHeapUsageAboveThresholdChartStatsVisitor Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jul 24, 2019</pre>
 */
public class JvmAvgHeapUsageAboveThresholdChartStatsVisitorTest
{

    /**
     * Method: getPercentThreshold()
     */
    @Test
    public void testGetPercentThreshold()
    throws Exception
    {
        JvmAvgHeapUsageAboveThresholdChartStatsVisitor v = new JvmAvgHeapUsageAboveThresholdChartStatsVisitor();
        v.setPercentThreshold(3);

        assertEquals(3, v.getPercentThreshold());
    }

    /**
     * Method: setPercentThreshold(int percentThreshold)
     */
    @Test
    public void testSetPercentThreshold()
    throws Exception
    {
        JvmAvgHeapUsageAboveThresholdChartStatsVisitor v = new JvmAvgHeapUsageAboveThresholdChartStatsVisitor();
        v.setPercentThreshold(40);

        assertEquals(40, v.getPercentThreshold());
    }

    @Test
    public void testGetChart()
    throws Exception
    {
        System.setProperty("DAY_FILTER", "06/03/2019");
        JvmAvgHeapUsageAboveThresholdChartStatsVisitor v = new JvmAvgHeapUsageAboveThresholdChartStatsVisitor();

        v.setPercentThreshold(2);

        StatsToChart statsToChart = new StatsToChart(v);

        Chart c = statsToChart.convert(Paths.get("src/test/resources/stats").toFile());


        String path = "target/avg.png";
        IO.delete(Paths.get(path).toFile());

        IO.writeFile(path, c.getBytes(), false);
        assertTrue(IO.exists(path));

    }


} 
