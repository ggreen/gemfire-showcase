package com.vmware.pivotal.labs.services.dataTx.geode.office;

import nyla.solutions.core.util.Config;
import nyla.solutions.office.chart.Chart;
import nyla.solutions.office.chart.JFreeChartFacade;

public abstract class AbstractChartVisitor implements ChartStatsVisitor{

    public static final int BYTES_GB = 1073741824;
    private int width = Config.getPropertyInteger("JVM_MEMORY_WIDTH",2000);
    private int height = Config.getPropertyInteger("JVM_MEMORY_HEIGHT",1500);;
    protected  final JFreeChartFacade chart ;

    protected AbstractChartVisitor() {
        this(new JFreeChartFacade());
    }

    protected AbstractChartVisitor(JFreeChartFacade chart) {
        this.chart = chart;
        this.chart.setHeight(height);
        this.chart.setWidth(width);
        this.chart.setLegend(true);
        this.chart.setTooltips(true);

    }



    /**
     *
     * @return the chart
     */
    public final Chart getChart()
    {
        return this.chart;
    }

    /**
     *
     * @param graphType the CHART..... graph type
     */
    public void setGraphType(String graphType)
    {
       this.chart.setGraphType(graphType);
    }
}
