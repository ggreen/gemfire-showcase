package com.vmware.data.services.gemfire.operations.stats.statInfo;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;

import java.io.PrintWriter;

abstract class BitZeroInterval extends BitInterval {
    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 4;
    }

    abstract long getBits();

    @Override
    int fill(double[] values, int valueOffset, int typeCode, int skipCount) {
      int fillcount = values.length - valueOffset; // space left in values
      int maxCount = count - skipCount; // maximum values this interval can produce
      if (fillcount > maxCount) {
        fillcount = maxCount;
      }
      double value = GfStatsReader.bitsToDouble(typeCode, getBits());
      for (int i = 0; i < fillcount; i++) {
        values[valueOffset + i] = value;
      }
      return fillcount;
    }

    @Override
    void dump(PrintWriter stream) {
      stream.print(getBits());
      if (count > 1) {
        stream.print("r" + count);
      }
    }

    BitZeroInterval(int count) {
      this.count = count;
    }

    @Override
    boolean attemptAdd(long addBits, long addInterval, int addCount) {
      // addCount >= 2; count >= 2
      if (addInterval == 0 && addBits == getBits()) {
        count += addCount;
        return true;
      }
      return false;
    }
  }