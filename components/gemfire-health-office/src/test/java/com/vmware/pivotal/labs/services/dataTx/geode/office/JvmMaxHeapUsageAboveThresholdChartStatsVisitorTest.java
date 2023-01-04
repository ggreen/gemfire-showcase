package com.vmware.pivotal.labs.services.dataTx.geode.office;

import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JvmMaxHeapUsageAboveThresholdChartStatsVisitorTest
{

    @Test
    public void testVisitResourceInst()
    throws Exception
    {
        System.setProperty("DAY_FILTER", "06/03/2019");
        JvmMaxHeapUsageAboveThresholdChartStatsVisitor v = new JvmMaxHeapUsageAboveThresholdChartStatsVisitor();

        v.setPercentThreshold(2);

        StatsToChart statsToChart = new StatsToChart(v);

        Chart c = statsToChart.convert(Paths.get("src/test/resources/stats").toFile());


        String path = "target/maxMemory.png";
        IO.delete(Paths.get(path).toFile());

        IO.writeFile(path, c.getBytes(), false);
        assertTrue(IO.exists(path));

    }

}
