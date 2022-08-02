package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.PrintWriter;

import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;


public class SimpleValue  extends AbstractValue implements StatsInfo
{
	private final ResourceInst resource;

private boolean useNextBits = false;
private long nextBits;
private final BitSeries series;
private boolean valueChangeNoticed = false;


public StatValue createTrimmed(long startTime, long endTime) {
  if (startTime == this.startTime && endTime == this.endTime) {
    return this;
  } else {
    return new SimpleValue(this, startTime, endTime);
  }
}

protected SimpleValue(ResourceInst resource, StatDescriptor sd) {
  this.resource = resource;
  if (sd.isCounter()) {
    this.filter = FILTER_PERSEC;
  } else {
    this.filter = FILTER_NONE;
  }
  this.descriptor = sd;
  this.series = new BitSeries();
  this.statsValid = false;
}

private SimpleValue(SimpleValue in, long startTime, long endTime) {
  this.startTime = startTime;
  this.endTime = endTime;
  this.useNextBits = in.useNextBits;
  this.nextBits = in.nextBits;
  this.resource = in.resource;
  this.series = in.series;
  this.descriptor = in.descriptor;
  this.filter = in.filter;
  this.statsValid = false;
  this.valueChangeNoticed = true;
}

public ResourceType getType() {
  return this.resource.getType();
}

public ResourceInst[] getResources() {
  return new ResourceInst[]{this.resource};
}

public boolean isTrimmedLeft() {
  return getStartIdx() != 0;
}

private int getStartIdx() {
  int startIdx = 0;
  if (startTime != -1) {
    long startTimeStamp = startTime - resource.getTimeBase();
    long[] timestamps = resource.getAllRawTimeStamps();
    for (int i = resource.getFirstTimeStampIdx();
         i < resource.getFirstTimeStampIdx() + series.getSize();
         i++) {
      if (timestamps[i] >= startTimeStamp) {
        break;
      }
      startIdx++;
    }
  }
  return startIdx;
}

private int getEndIdx(int startIdx) {
  int endIdx = series.getSize() - 1;
  if (endTime != -1) {
    long endTimeStamp = endTime - resource.getTimeBase();
    long[] timestamps = resource.getAllRawTimeStamps();
    endIdx = startIdx - 1;
    for (int i = resource.getFirstTimeStampIdx() + startIdx;
         i < resource.getFirstTimeStampIdx() + series.getSize();
         i++) {
      if (timestamps[i] >= endTimeStamp) {
        break;
      }
      endIdx++;
    }
    assert (endIdx == startIdx - 1 || timestamps[endIdx] < endTimeStamp);
  }
  return endIdx;
}

public double[] getSnapshots() {
  double[] result;
  int startIdx = getStartIdx();
  int endIdx = getEndIdx(startIdx);
  int resultSize = (endIdx - startIdx) + 1;

  if (filter != FILTER_NONE && resultSize > 1) {
    long[] timestamps = null;
    if (filter == FILTER_PERSEC) {
      timestamps = resource.getAllRawTimeStamps();
    }
    result = new double[resultSize - 1];
    int tsIdx = resource.getFirstTimeStampIdx() + startIdx;
    double[] values = series.getValuesEx(descriptor.getTypeCode(), startIdx,
        resultSize);
    for (int i = 0; i < result.length; i++) {
      double valueDelta = values[i + 1] - values[i];
      if (filter == FILTER_PERSEC) {
        double timeDelta = (timestamps[tsIdx + i + 1] - timestamps[tsIdx + i]); // millis
        valueDelta /= (timeDelta / 1000); // per second
      }
      result[i] = valueDelta;
    }
  } else {
    result = series.getValuesEx(descriptor.getTypeCode(), startIdx,
        resultSize);
  }
  calcStats(result);
  return result;
}

public double[] getRawSnapshots() {
  int startIdx = getStartIdx();
  int endIdx = getEndIdx(startIdx);
  int resultSize = (endIdx - startIdx) + 1;
  return series.getValuesEx(descriptor.getTypeCode(), startIdx, resultSize);
}

public long[] getRawAbsoluteTimeStampsWithSecondRes() {
  long[] result = getRawAbsoluteTimeStamps();
  for (int i = 0; i < result.length; i++) {
    result[i] += 500;
    result[i] /= 1000;
    result[i] *= 1000;
  }
  return result;
}

public long[] getRawAbsoluteTimeStamps() {
  int startIdx = getStartIdx();
  int endIdx = getEndIdx(startIdx);
  int resultSize = (endIdx - startIdx) + 1;
  if (resultSize <= 0) {
    return new long[0];
  } else {
    long[] result = new long[resultSize];
    long[] timestamps = resource.getAllRawTimeStamps();
    int tsIdx = resource.getFirstTimeStampIdx() + startIdx;
    long base = resource.getTimeBase();
    for (int i = 0; i < resultSize; i++) {
      result[i] = base + timestamps[tsIdx + i];
    }
    return result;
  }
}

public boolean hasValueChanged() {
  if (valueChangeNoticed) {
    valueChangeNoticed = false;
    return true;
  } else {
    return false;
  }
}

protected int getMemoryUsed() {
  int result = 0;
  if (series != null) {
    result += series.getMemoryUsed();
  }
  return result;
}

protected void dump(PrintWriter stream) {
  calcStats();
  stream.print("  " + descriptor.getName() + "=");
  stream.print("[size=" + getSnapshotsSize()
      + " min=" + nf.format(min)
      + " max=" + nf.format(max)
      + " avg=" + nf.format(avg)
      + " stddev=" + nf.format(stddev) + "]");
  if (Boolean.getBoolean("StatArchiveReader.dumpall")) {
    series.dump(stream);
  } else {
    stream.println();
  }
}

protected void shrink() {
  this.series.shrink();
}

protected void initialValue(long v) {
  this.series.initialBits(v);
}

protected void prepareNextBits(long bits) {
  useNextBits = true;
  nextBits = bits;
}

protected void addSample() {
  statsValid = false;
  if (useNextBits) {
    useNextBits = false;
    series.addBits(nextBits);
    valueChangeNoticed = true;
  } else {
    series.addBits(0);
  }
}
	@Override
	public void accept(StatsVisitor visitor)
	{
		// TODO Auto-generated method stub

	}

}
