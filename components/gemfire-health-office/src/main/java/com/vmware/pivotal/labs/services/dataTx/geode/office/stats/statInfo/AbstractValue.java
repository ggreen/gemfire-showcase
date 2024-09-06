package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;


import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.GfStatsReader;

import java.text.NumberFormat;
import java.util.Date;

abstract class AbstractValue implements StatValue
{
	
	protected static final NumberFormat nf = NumberFormat.getNumberInstance();
	 
    protected StatDescriptor descriptor;
    protected int filter;

    protected long startTime = -1;
    protected long endTime = -1;

    protected boolean statsValid = false;
    protected int size;
    protected double min;
    protected double max;
    protected double avg;
    protected double stddev;
    protected double mostRecent;

    public void calcStats() {
      if (!statsValid) {
        getSnapshots();
      }
    }

    public int getSnapshotsSize() {
      calcStats();
      return this.size;
    }

    public double getSnapshotsMinimum() {
      calcStats();
      return this.min;
    }

    public double getSnapshotsMaximum() {
      calcStats();
      return this.max;
    }

    public double getSnapshotsAverage() {
      calcStats();
      return this.avg;
    }

    public double getSnapshotsStandardDeviation() {
      calcStats();
      return this.stddev;
    }

    public double getSnapshotsMostRecent() {
      calcStats();
      return this.mostRecent;
    }

    public StatDescriptor getDescriptor() {
      return this.descriptor;
    }

    public int getFilter() {
      return this.filter;
    }

    public void setFilter(int filter) {
      if (filter != this.filter) {
        if (filter != FILTER_NONE
            && filter != FILTER_PERSEC
            && filter != FILTER_PERSAMPLE) {
          throw new IllegalArgumentException("Filter value " + filter
              + " must be " + FILTER_NONE + ", " + FILTER_PERSEC + " or "
              + FILTER_PERSAMPLE);
        }
        this.filter = filter;
        this.statsValid = false;
      }
    }

    /*
     * Calculates each stat given the result of calling getSnapshots
     */
    protected void calcStats(double[] values) {
      if (statsValid) {
        return;
      }
      size = values.length;
      if (size == 0) {
        min = 0.0;
        max = 0.0;
        avg = 0.0;
        stddev = 0.0;
        mostRecent = 0.0;
      } else {
        min = values[0];
        max = values[0];
        mostRecent = values[values.length - 1];
        double total = values[0];
        for (int i = 1; i < size; i++) {
          total += values[i];
          if (values[i] < min) {
            min = values[i];
          } else if (values[i] > max) {
            max = values[i];
          }
        }
        avg = total / size;
        stddev = 0.0;
        if (size > 1) {
          for (int i = 0; i < size; i++) {
            double dv = values[i] - avg;
            stddev += (dv * dv);
          }
          stddev /= (size - 1);
          stddev = Math.sqrt(stddev);
        }
      }
      statsValid = true;
    }

    /*
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
      calcStats();
      StringBuilder result = new StringBuilder();
      result.append(getDescriptor().getName());
      String units = getDescriptor().getUnits();
      if (units != null && units.length() > 0) {
        result.append(' ').append(units);
      }
      if (filter == FILTER_PERSEC) {
        result.append("/sec");
      } else if (filter == FILTER_PERSAMPLE) {
        result.append("/sample");
      }
      result.append(": samples=")
          .append(getSnapshotsSize());
      if (startTime != -1) {
        result.append(" startTime=\"")
            .append(new Date(startTime))
            .append("\"");
      }
      if (endTime != -1) {
        result.append(" endTime=\"")
            .append(new Date(endTime))
            .append("\"");
      }
      result.append(" min=")
          .append(GfStatsReader.getNumberFormat().format(min));
      result.append(" max=")
          .append(GfStatsReader.getNumberFormat().format(max));
      result.append(" average=")
          .append(GfStatsReader.getNumberFormat().format(avg));
      result.append(" stddev=")
          .append(GfStatsReader.getNumberFormat().format(stddev));
      result.append(" last=") // for bug 42532
          .append(GfStatsReader.getNumberFormat().format(mostRecent));
      return result.toString();
    }
  }