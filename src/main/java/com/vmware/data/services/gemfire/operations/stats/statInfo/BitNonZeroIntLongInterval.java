package com.vmware.data.services.gemfire.operations.stats.statInfo;

class BitNonZeroIntLongInterval extends BitNonZeroInterval {
    int bits;
    long interval;

    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 12;
    }

    @Override
    long getBits() {
      return this.bits;
    }

    @Override
    long getInterval() {
      return this.interval;
    }

    BitNonZeroIntLongInterval(int bits, long interval, int count) {
      super(count);
      this.bits = bits;
      this.interval = interval;
    }
  }