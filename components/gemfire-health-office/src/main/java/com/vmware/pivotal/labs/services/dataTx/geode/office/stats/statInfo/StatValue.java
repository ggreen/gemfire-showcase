package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;

public interface StatValue {

    public static final int FILTER_NONE = 0;

    public static final int FILTER_PERSEC = 1;

    public static final int FILTER_PERSAMPLE = 2;

    public StatValue createTrimmed(long startTime, long endTime);

    public boolean isTrimmedLeft();


    public ResourceType getType();


    public ResourceInst[] getResources();


    public long[] getRawAbsoluteTimeStamps();

    /**
     * @return an array of timestamps for each unfiltered snapshot in this
     * value. Each returned time stamp is the number of millis since midnight,
     * Jan 1, 1970 UTC. The resolution is seconds.
     */
    public long[] getRawAbsoluteTimeStampsWithSecondRes();

    /**
     * @return an array of doubles containing the unfiltered value of this
     * statistic for each point in time that it was sampled.
     */
    public double[] getRawSnapshots();

    /**
     * @return an array of doubles containing the filtered value of this
     * statistic for each point in time that it was sampled.
     */
    public double[] getSnapshots();

    /**
     * @return the number of samples taken of this statistic's value.
     */
    public int getSnapshotsSize();

    /**
     * @return the smallest of all the samples taken of this statistic's value.
     */
    public double getSnapshotsMinimum();

    /**
     * @return the largest of all the samples taken of this statistic's value.
     */
    public double getSnapshotsMaximum();

    /**
     * @return the average of all the samples taken of this statistic's value.
     */
    public double getSnapshotsAverage();

    /**
     * @return the standard deviation of all the samples taken of this
     * statistic's value.
     */
    public double getSnapshotsStandardDeviation();

    /**
     * @return the most recent value of all the samples taken of this
     * statistic's value.
     */
    public double getSnapshotsMostRecent();

    /**
     * @return true if sample whose value was different from previous values has
     * been added to this StatValue since the last time this method was called.
     */
    public boolean hasValueChanged();

    /**
     * @return the current filter used to calculate this statistic's values. It
     * will be one of these values: <ul> <li> {@link #FILTER_NONE} <li> {@link
     * #FILTER_PERSAMPLE} <li> {@link #FILTER_PERSEC} </ul>
     */
    public int getFilter();


    public void setFilter(int filter);

    /**
     * @return a description of this statistic.
     */
    public StatDescriptor getDescriptor();
  }