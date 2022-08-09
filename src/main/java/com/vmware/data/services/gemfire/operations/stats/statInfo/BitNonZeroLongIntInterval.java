package com.vmware.data.services.gemfire.operations.stats.statInfo;

class BitNonZeroLongIntInterval extends BitNonZeroInterval {
    long bits;
    int interval;

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

    BitNonZeroLongIntInterval(long bits, int interval, int count) {
      super(count);
      this.bits = bits;
      this.interval = interval;
    }
  }