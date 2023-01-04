package com.vmware.pivotal.labs.services.dataTx.geode.office;

import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * CpuAboveThresholdChartStatsVisitor Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Jul 27, 2019</pre>
 */
public class CpuAboveThresholdChartStatsVisitorTest
{

    /**
     * Method: visitResourceInst(ResourceInst resourceInst)
     */
    @Test
    public void testVisitResourceInst()
    throws Exception
    {


        CpuAboveThresholdChartStatsVisitor v = new CpuAboveThresholdChartStatsVisitor
                (new Day("6/3/2018"), 1);

        StatsToChart toChart = new StatsToChart(v);

        Chart chart = toChart.convert(Paths.get("src/test/resources/stats").toFile());


        IO.delete(new File("target/reports/cpu.png"));

        IO.mkdir("target/reports");

        IO.writeFile("target/reports/cpu.png", chart.getBytes());

        assertEquals(true, IO.exists("target/reports/cpu.png"));
    }


} 
