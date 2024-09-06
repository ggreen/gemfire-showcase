package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;


import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.GfStatsReader;

import java.io.PrintWriter;

class BitExplicitLongInterval extends BitInterval {
    long[] bitArray = null;

    @Override
    int getMemoryUsed() {
      int result = super.getMemoryUsed() + 4 + 4;
      if (bitArray != null) {
        result += bitArray.length * 8;
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
      for (int i = 0; i < fillcount; i++) {
        values[valueOffset + i] = GfStatsReader.bitsToDouble(typeCode,
            bitArray[skipCount + i]);
      }
      return fillcount;
    }

    @Override
    void dump(PrintWriter stream) {
      stream.print("(count=" + count + " ");
      for (int i = 0; i < count; i++) {
        if (i != 0) {
          stream.print(", ");
        }
        stream.print(bitArray[i]);
      }
      stream.print(")");
    }

    BitExplicitLongInterval(long bits, long interval, int addCount) {
      count = addCount;
      bitArray = new long[count * 2];
      for (int i = 0; i < count; i++) {
        bitArray[i] = bits;
        bits += interval;
      }
    }

    @Override
    boolean attemptAdd(long addBits, long addInterval, int addCount) {
      // addCount >= 2; count >= 2
      if (addCount <= 3) {
        if ((count + addCount) >= bitArray.length) {
          long[] tmp = new long[(count + addCount) * 2];
          System.arraycopy(bitArray, 0, tmp, 0, bitArray.length);
          bitArray = tmp;
        }
        for (int i = 0; i < addCount; i++) {
          bitArray[count++] = addBits;
          addBits += addInterval;
        }
        return true;
      }
      return false;
    }
  }