package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.PrintWriter;

import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;


public abstract class BitInterval implements StatsInfo
{
    abstract int fill(double[] values, int valueOffset, int typeCode,
        int skipCount);

    abstract void dump(PrintWriter stream);

    abstract boolean attemptAdd(long addBits, long addInterval, int addCount);

    int getMemoryUsed() {
      return 0;
    }

    protected int count;

    public final int getSampleCount() {
      return this.count;
    }

    static BitInterval create(long bits, long interval, int count) {
      if (interval == 0) {
        if (bits <= Integer.MAX_VALUE && bits >= Integer.MIN_VALUE) {
          return new BitZeroIntInterval((int) bits, count);
        } else {
          return new BitZeroLongInterval(bits, count);
        }
      } else if (count <= 3) {
        if (interval <= Byte.MAX_VALUE && interval >= Byte.MIN_VALUE) {
          return new BitExplicitByteInterval(bits, interval, count);
        } else if (interval <= Short.MAX_VALUE && interval >= Short.MIN_VALUE) {
          return new BitExplicitShortInterval(bits, interval, count);
        } else if (interval <= Integer.MAX_VALUE && interval >= Integer.MIN_VALUE) {
          return new BitExplicitIntInterval(bits, interval, count);
        } else {
          return new BitExplicitLongInterval(bits, interval, count);
        }
      } else {
        boolean smallBits = false;
        boolean smallInterval = false;
        if (bits <= Integer.MAX_VALUE && bits >= Integer.MIN_VALUE) {
          smallBits = true;
        }
        if (interval <= Integer.MAX_VALUE && interval >= Integer.MIN_VALUE) {
          smallInterval = true;
        }
        if (smallBits) {
          if (smallInterval) {
            return new BitNonZeroIntIntInterval((int) bits, (int) interval,
                count);
          } else {
            return new BitNonZeroIntLongInterval((int) bits, interval, count);
          }
        } else {
          if (smallInterval) {
            return new BitNonZeroLongIntInterval(bits, (int) interval, count);
          } else {
            return new BitNonZeroLongLongInterval(bits, interval, count);
          }
        }
      }
    }

	@Override
	public void accept(StatsVisitor visitor)
	{
		// TODO Auto-generated method stub
		
	}
}
