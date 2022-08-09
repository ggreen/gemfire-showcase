package com.vmware.data.services.gemfire.operations.stats.statInfo;

import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;

import java.io.PrintWriter;
import java.util.List;


/*
 * Defines a single instance of a resource type.
 */
public class ResourceInst
{
	   private final boolean loaded;
	    private final GfStatsReader archive;
	    //    private final int uniqueId;
	    private final ResourceType type;
	    private final String name;
	    private final long id;
	    private boolean active = true;
	    private final SimpleValue[] values;
	    private int firstTSidx = -1;
	    private int lastTSidx = -1;

	    /*
	     * Returns the approximate amount of memory used to implement this object.
	     */
	    public int getMemoryUsed() {
	      int result = 0;
	      if (values != null) {
	        for (SimpleValue value : values) {
	          result += value.getMemoryUsed();
	        }
	      }
	      return result;
	    }

	    /*
	     * Returns a string representation of this object.
	     */
	    @Override
	    public String toString() {
	      StringBuilder result = new StringBuilder();
	      result.append(name)
	          .append(", ")
	          .append(id)
	          .append(", ")
	          .append(type.getName())
	          .append(": \"")
	          .append(archive.formatTimeMillis(getFirstTimeMillis()))
	          .append('\"');
	      if (!active) {
	        result.append(" inactive");
	      }
	      result.append(" samples=").append(getSampleCount());
	      return result.toString();
	    }

	    /*
	     * Returns the number of times this resource instance has been sampled.
	     */
	    public int getSampleCount() {
	      if (active) {
	        return archive.getTimeStamps().getSize() - firstTSidx;
	      } else {
	        return (lastTSidx + 1) - firstTSidx;
	      }
	    }

	    public GfStatsReader getArchive() {
	      return this.archive;
	    }

	    public void dump(PrintWriter stream) {
	      stream.println(name + ":"
	          + " file=" + getArchive().getArchiveFileName()
	          + " id=" + id
	          + (active ? "" : " deleted")
	          + " start=" + archive.formatTimeMillis(getFirstTimeMillis()));
	      for (SimpleValue value : values) {
	        value.dump(stream);
	      }
	    }

	    public ResourceInst(GfStatsReader archive, String name,
	        long id, ResourceType type, boolean loaded) {
	      this.loaded = loaded;
	      this.archive = archive;
	      this.name = name;
	      this.id = id;
	      assert (type != null);
	      this.type = type;
	      if (loaded) {
	        StatDescriptor[] stats = type.getStats();
	        this.values = new SimpleValue[stats.length];
	        for (int i = 0; i < stats.length; i++) {
	          if (archive.loadStat(stats[i], this)) {
	            this.values[i] = new SimpleValue(this, stats[i]);
	          } else {
	            this.values[i] = null;
	          }
	        }
	      } else {
	        this.values = null;
	      }
	    }

	    public void matchSpec(StatSpec spec, List<StatValue> matchedValues) {
	      if (spec.typeMatches(this.type.getName())) {
	        if (spec.instanceMatches(this.getName(), this.getId())) {
	          for (SimpleValue value : values) {
	            if (value != null) {
	              if (spec.statMatches(value.getDescriptor().getName())) {
	                matchedValues.add(value);
	              }
	            }
	          }
	        }
	      }
	    }

	    public void initialValue(int statOffset, long v) {
	      if (this.values != null && this.values[statOffset] != null) {
	        this.values[statOffset].initialValue(v);
	      }
	    }

	    /*
	     * Returns true if sample was added.
	     */
	    public boolean addValueSample(int statOffset, long statDeltaBits) {
	      if (this.values != null && this.values[statOffset] != null) {
	        this.values[statOffset].prepareNextBits(statDeltaBits);
	        return true;
	      } else {
	        return false;
	      }
	    }

	    public boolean isLoaded() {
	      return this.loaded;
	    }

	    /*
	     * Frees up any resources no longer needed after the archive file is closed.
	     * Returns true if this guy is no longer needed.
	     */
	    public boolean close() {
	      if (isLoaded()) {
	        for (SimpleValue value : values) {
	          if (value != null) {
	            value.shrink();
	          }
	        }
	        return false;
	      } else {
	        return true;
	      }
	    }

	    protected int getFirstTimeStampIdx() {
	      return this.firstTSidx;
	    }

	    protected long[] getAllRawTimeStamps() {
	      return archive.getTimeStamps().getRawTimeStamps();
	    }

	    protected long getTimeBase() {
	      return archive.getTimeStamps().getBase();
	    }

	    /*
	     * Returns an array of doubles containing the timestamps at which this
	     * instances samples where taken. Each of these timestamps is the
	     * difference, measured in milliseconds, between the sample time and
	     * midnight, January 1, 1970 UTC. Although these values are double they can
	     * safely be converted to <code>long</code> with no loss of information.
	     */
	    public double[] getSnapshotTimesMillis() {
	      return archive.getTimeStamps().getTimeValuesSinceIdx(firstTSidx);
	    }

	    /*
	     * Returns an array of statistic value descriptors. Each element of the
	     * array describes the corresponding statistic this instance supports. The
	     * <code>StatValue</code> instances can be used to obtain the actual sampled
	     * values of the instances statistics.
	     */
	    public StatValue[] getStatValues() {
	      return this.values;
	    }

	    /*
	     * Gets the value of the stat in the current instance given the stat name.
	     *
	     * @param name the name of the stat to find in the current instance
	     * @return the value that matches the name or null if the instance does not
	     * have a stat of the given name
	     */
	    public StatValue getStatValue(String name) {
	      StatValue result = null;
	      StatDescriptor desc = getType().getStat(name);
	      if (desc != null) {
	        result = values[desc.getOffset()];
	      }
	      return result;
	    }

	    /*
	     * Returns the name of this instance.
	     */
	    public String getName() {
	      return this.name;
	    }

	    /*
	     * Returns the id of this instance.
	     */
	    public long getId() {
	      return this.id;
	    }

	    /*
	     * Returns the difference, measured in milliseconds, between the time of the
	     * instance's first sample and midnight, January 1, 1970 UTC.
	     */
	    public long getFirstTimeMillis() {
	      return archive.getTimeStamps().getMilliTimeStamp(firstTSidx);
	    }

	    /*
	     * Returns resource type of this instance.
	     */
	    public ResourceType getType() {
	      return this.type;
	    }

	    public void makeInactive() {
	      this.active = false;
	      lastTSidx = archive.getTimeStamps().getSize() - 1;
	      close(); // this frees up unused memory now that no more samples
	    }

	    /*
	     * Returns true if archive might still have future samples for this
	     * instance.
	     */
	    public boolean isActive() {
	      return this.active;
	    }

	    public void addTimeStamp() {
	      if (this.loaded) {
	        if (firstTSidx == -1) {
	          firstTSidx = archive.getTimeStamps().getSize() - 1;
	        }
	        for (SimpleValue value : values) {
	          if (value != null) {
	            value.addSample();
	          }
	        }
	      }
	    }

	    @Override
	    public int hashCode() {
	      final int prime = 31;
	      int result = 1;
	      result = prime * result + (int) (id ^ (id >>> 32));
	      result = prime * result + ((name == null) ? 0 : name.hashCode());
	      result = prime * result + ((type == null) ? 0 : type.hashCode());
	      return result;
	    }

	    @Override
	    public boolean equals(Object obj) {
	      if (this == obj)
	        return true;
	      if (obj == null)
	        return false;
	      if (getClass() != obj.getClass())
	        return false;
	      ResourceInst other = (ResourceInst) obj;
	      if (id != other.id)
	        return false;
	      if (name == null) {
	        if (other.name != null)
	          return false;
	      } else if (!name.equals(other.name))
	        return false;
	      if (type == null) {
	        if (other.type != null)
	          return false;
	      } else if (!type.equals(other.type))
	        return false;
	      return true;
	    }
}
