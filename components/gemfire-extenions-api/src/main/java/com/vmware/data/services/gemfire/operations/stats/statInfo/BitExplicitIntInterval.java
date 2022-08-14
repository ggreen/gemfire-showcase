package com.vmware.data.services.gemfire.operations.stats.statInfo;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;

import java.io.PrintWriter;


class BitExplicitIntInterval extends BitInterval {
    long firstValue;
    long lastValue;
    int[] bitIntervals = null;

    @Override
    int getMemoryUsed() {
      int result = super.getMemoryUsed() + 4 + 8 + 8 + 4;
      if (bitIntervals != null) {
        result += bitIntervals.length * 4;
      }
      return result;
    }

    @Override
    int fill(double[] values, int valueOffset, int typeCode, int skipCount) {
      int fillcount = values.length - valueOffset; // space left in values
      int maxCount = count - skipCount; // maximum values this interval can produce
      if (fillcount > maxCount) {
        fillcount = maxCount;
      }
      long bitValue = firstValue;
      for (int i = 0; i < skipCount; i++) {
        bitValue += bitIntervals[i];
      }
      for (int i = 0; i < fillcount; i++) {
        bitValue += bitIntervals[skipCount + i];
        values[valueOffset + i] = GfStatsReader.bitsToDouble(typeCode, bitValue);
      }
      return fillcount;
    }

    @Override
    void dump(PrintWriter stream) {
      stream.print("(intIntervalCount=" + count + " start=" + firstValue);
      for (int i = 0; i < count; i++) {
        if (i != 0) {
          stream.print(", ");
        }
        stream.print(bitIntervals[i]);
      }
      stream.print(")");
    }

    BitExplicitIntInterval(long bits, long interval, int addCount) {
      count = addCount;
      firstValue = bits;
      lastValue = bits + (interval * (addCount - 1));
      bitIntervals = new int[count * 2];
      bitIntervals[0] = 0;
      for (int i = 1; i < count; i++) {
        bitIntervals[i] = (int) interval;
      }
    }

    @Override
    boolean attemptAdd(long addBits, long addInterval, int addCount) {
      // addCount >= 2; count >= 2
      if (addCount <= 4) {
        if (addInterval <= Integer.MAX_VALUE && addInterval >= Integer.MIN_VALUE) {
          long firstInterval = addBits - lastValue;
          if (firstInterval <= Integer.MAX_VALUE && firstInterval >= Integer.MIN_VALUE) {
            lastValue = addBits + (addInterval * (addCount - 1));
            if ((count + addCount) >= bitIntervals.length) {
              int[] tmp = new int[(count + addCount) * 2];
              System.arraycopy(bitIntervals, 0, tmp, 0, bitIntervals.length);
              bitIntervals = tmp;
            }
            bitIntervals[count++] = (int) firstInterval;
            for (int i = 1; i < addCount; i++) {
              bitIntervals[count++] = (int) addInterval;
            }
            return true;
          }
        }
      }
      return false;
    }
  }