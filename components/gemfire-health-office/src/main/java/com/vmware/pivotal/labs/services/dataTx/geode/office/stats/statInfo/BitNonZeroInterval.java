package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;


import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.GfStatsReader;

import java.io.PrintWriter;

abstract class BitNonZeroInterval extends BitInterval {
    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 4;
    }

    abstract long getBits();

    abstract long getInterval();

    @Override
    int fill(double[] values, int valueOffset, int typeCode, int skipCount) {
      int fillcount = values.length - valueOffset; // space left in values
      int maxCount = count - skipCount; // maximum values this interval can produce
      if (fillcount > maxCount) {
        fillcount = maxCount;
      }
      long base = getBits();
      long interval = getInterval();
      base += skipCount * interval;
      for (int i = 0; i < fillcount; i++) {
        values[valueOffset + i] = GfStatsReader.bitsToDouble(typeCode, base);
        base += interval;
      }
      return fillcount;
    }

    @Override
    void dump(PrintWriter stream) {
      stream.print(getBits());
      if (count > 1) {
        long interval = getInterval();
        if (interval != 0) {
          stream.print("+=" + interval);
        }
        stream.print("r" + count);
      }
    }

    BitNonZeroInterval(int count) {
      this.count = count;
    }

    @Override
    boolean attemptAdd(long addBits, long addInterval, int addCount) {
      // addCount >= 2; count >= 2
      if (addInterval == getInterval()) {
        if (addBits == (getBits() + (addInterval * (count - 1)))) {
          count += addCount;
          return true;
        }
      }
      return false;
    }
  }