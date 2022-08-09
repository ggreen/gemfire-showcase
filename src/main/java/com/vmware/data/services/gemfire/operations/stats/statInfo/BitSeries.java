package com.vmware.data.services.gemfire.operations.stats.statInfo;

import java.io.PrintWriter;

import com.vmware.data.services.gemfire.operations.stats.visitors.StatsVisitor;

public class BitSeries implements StatsInfo
{
	   int count; // number of items in this series
	    long currentStartBits;
	    long currentEndBits;
	    long currentInterval;
	    int currentCount;
	    int intervalIdx; // index of most recent BitInterval
	    BitInterval intervals[];

	    /*
	     * Returns the amount of memory used to implement this series.
	     */
	    protected int getMemoryUsed() {
	      int result = 4 + 8 + 8 + 8 + 4 + 4 + 4;
	      if (intervals != null) {
	        result += 4 * intervals.length;
	        for (int i = 0; i <= intervalIdx; i++) {
	          result += intervals[i].getMemoryUsed();
	        }
	      }
	      return result;
	    }

	    /*
	     * Gets the first "resultSize" values of this series skipping over the first
	     * "samplesToSkip" ones. The first value in a series is at index 0. The
	     * maximum result size can be obtained by calling "getSize()".
	     */
	    public double[] getValuesEx(int typeCode, int samplesToSkip,
	        int resultSize) {
	      double[] result = new double[resultSize];
	      int firstInterval = 0;
	      int idx = 0;
	      while (samplesToSkip > 0
	          && firstInterval <= intervalIdx
	          && intervals[firstInterval].getSampleCount() <= samplesToSkip) {
	        samplesToSkip -= intervals[firstInterval].getSampleCount();
	        firstInterval++;
	      }
	      for (int i = firstInterval; i <= intervalIdx; i++) {
	        idx += intervals[i].fill(result, idx, typeCode, samplesToSkip);
	        samplesToSkip = 0;
	      }
	      if (currentCount != 0) {
	        idx += BitInterval.create(currentStartBits, currentInterval,
	            currentCount).fill(result, idx, typeCode, samplesToSkip);
	      }
	      // assert
	      if (idx != resultSize) {
	        throw new RuntimeException("GetValuesEx didn't fill the last "
	            + (resultSize - idx) + " entries of its result");
	      }
	      return result;
	    }

	    void dump(PrintWriter stream) {
	      stream.print("[size=" + count + " intervals=" + (intervalIdx + 1)
	          + " memused=" + getMemoryUsed() + " ");
	      for (int i = 0; i <= intervalIdx; i++) {
	        if (i != 0) {
	          stream.print(", ");
	        }
	        intervals[i].dump(stream);
	      }
	      if (currentCount != 0) {
	        if (intervalIdx != -1) {
	          stream.print(", ");
	        }
	        BitInterval.create(currentStartBits, currentInterval,
	            currentCount).dump(stream);
	      }
	      stream.println("]");
	    }

	    BitSeries() {
	      count = 0;
	      currentStartBits = 0;
	      currentEndBits = 0;
	      currentInterval = 0;
	      currentCount = 0;
	      intervalIdx = -1;
	      intervals = null;
	    }

	    void initialBits(long bits) {
	      this.currentEndBits = bits;
	    }

	    int getSize() {
	      return this.count;
	    }

	    void addBits(long deltaBits) {
	      long bits = currentEndBits + deltaBits;
	      if (currentCount == 0) {
	        currentStartBits = bits;
	        currentCount = 1;
	      } else if (currentCount == 1) {
	        currentInterval = deltaBits;
	        currentCount++;
	      } else if (deltaBits == currentInterval) {
	        currentCount++;
	      } else {
	        // we need to move currentBits into a BitInterval
	        if (intervalIdx == -1) {
	          intervals = new BitInterval[2];
	          intervalIdx = 0;
	          intervals[0] = BitInterval.create(currentStartBits, currentInterval,
	              currentCount);
	        } else {
	          if (!intervals[intervalIdx].attemptAdd(currentStartBits,
	              currentInterval, currentCount)) {
	            // wouldn't fit in current bit interval so add a new one
	            intervalIdx++;
	            if (intervalIdx >= intervals.length) {
	              BitInterval[] tmp = new BitInterval[intervals.length * 2];
	              System.arraycopy(intervals, 0, tmp, 0, intervals.length);
	              intervals = tmp;
	            }
	            intervals[intervalIdx] = BitInterval.create(currentStartBits,
	                currentInterval, currentCount);
	          }
	        }
	        // now start a new currentBits
	        currentStartBits = bits;
	        currentCount = 1;
	      }
	      currentEndBits = bits;
	      count++;
	    }

	    /*
	     * Free up any unused memory
	     */
	    void shrink() {
	      if (intervals != null) {
	        int currentSize = intervalIdx + 1;
	        if (currentSize < intervals.length) {
	          BitInterval[] tmp = new BitInterval[currentSize];
	          System.arraycopy(intervals, 0, tmp, 0, currentSize);
	          intervals = tmp;
	        }
	      }
	    }
	    
	@Override
	public void accept(StatsVisitor visitor)
	{
		// TODO Auto-generated method stub

	}

}
