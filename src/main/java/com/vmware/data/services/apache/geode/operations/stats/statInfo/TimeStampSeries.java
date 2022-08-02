package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.PrintWriter;

import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;

public class TimeStampSeries implements StatsInfo
{
	 static private final int GROW_SIZE = 256;
	    int count; // number of items in this series
	    long base; // millis since midnight, Jan 1, 1970 UTC.
	    long[] timeStamps = new long[GROW_SIZE]; // elapsed millis from base

	    public void dump(PrintWriter stream) {
	      stream.print("[size=" + count);
	      for (int i = 0; i < count; i++) {
	        if (i != 0) {
	          stream.print(", ");
	          stream.print(timeStamps[i] - timeStamps[i - 1]);
	        } else {
	          stream.print(" " + timeStamps[i]);
	        }
	      }
	      stream.println("]");
	    }

	    public void shrink() {
	      if (count < timeStamps.length) {
	        long[] tmp = new long[count];
	        System.arraycopy(timeStamps, 0, tmp, 0, count);
	        timeStamps = tmp;
	      }
	    }

	    public TimeStampSeries() {
	      count = 0;
	      base = 0;
	    }

	    public void setBase(long base) {
	      this.base = base;
	    }

	    int getSize() {
	      return this.count;
	    }

	    public void addTimeStamp(int ts) {
	      if (count >= timeStamps.length) {
	        long[] tmp = new long[timeStamps.length + GROW_SIZE];
	        System.arraycopy(timeStamps, 0, tmp, 0, timeStamps.length);
	        timeStamps = tmp;
	      }
	      if (count != 0) {
	        timeStamps[count] = timeStamps[count - 1] + ts;
	      } else {
	        timeStamps[count] = ts;
	      }
	      count++;
	    }

	    long getBase() {
	      return this.base;
	    }

	    /**
	     * Provides direct access to underlying data. Do not modify contents and use
	     * getSize() to keep from reading past end of array.
	     */
	    long[] getRawTimeStamps() {
	      return this.timeStamps;
	    }

	    long getMilliTimeStamp(int idx) {
	      return this.base + this.timeStamps[idx];
	    }

	    /**
	     * Returns an array of time stamp values the first of which has the
	     * specified index. Each returned time stamp is the number of millis since
	     * midnight, Jan 1, 1970 UTC.
	     */
	    double[] getTimeValuesSinceIdx(int idx) {
	      int resultSize = this.count - idx;
	      double[] result = new double[resultSize];
	      for (int i = 0; i < resultSize; i++) {
	        result[i] = getMilliTimeStamp(idx + i);
	      }
	      return result;
	    }

		@Override
		public void accept(StatsVisitor visitor)
		{
			visitor.visitTimeStampSeries(this);
			
		}

}
