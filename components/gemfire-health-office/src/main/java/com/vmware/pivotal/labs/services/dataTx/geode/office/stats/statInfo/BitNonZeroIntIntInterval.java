package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;

class BitNonZeroIntIntInterval extends BitNonZeroInterval {
    int bits;
    int interval;

    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 8;
    }

    @Override
    long getBits() {
      return this.bits;
    }

    @Override
    long getInterval() {
      return this.interval;
    }

    BitNonZeroIntIntInterval(int bits, int interval, int count) {
      super(count);
      this.bits = bits;
      this.interval = interval;
    }
  }