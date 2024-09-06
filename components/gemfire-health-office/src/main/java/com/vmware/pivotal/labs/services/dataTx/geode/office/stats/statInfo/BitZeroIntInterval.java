package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;

class BitZeroIntInterval extends BitZeroInterval {
    int bits;

    @Override
    int getMemoryUsed() {
      return super.getMemoryUsed() + 4;
    }

    @Override
    long getBits() {
      return bits;
    }

    BitZeroIntInterval(int bits, int count) {
      super(count);
      this.bits = bits;
    }
  }