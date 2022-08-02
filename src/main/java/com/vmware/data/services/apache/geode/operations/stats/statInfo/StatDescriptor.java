package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.PrintWriter;

/**
   * Describes a single statistic.
   */
  public class StatDescriptor {
    private boolean loaded;
    private String name;
    private final int offset;
    private final boolean isCounter;
    private final boolean largerBetter;
    private final byte typeCode;
    private String units;
    private String desc;

    protected void dump(PrintWriter stream) {
      stream.println("  " + name + ": type=" + typeCode + " offset=" + offset
          + (isCounter ? " counter" : "")
          + " units=" + units
          + " largerBetter=" + largerBetter
          + " desc=" + desc);
    }

    protected StatDescriptor(String name, int offset, boolean isCounter,
        boolean largerBetter,
        byte typeCode, String units, String desc) {
      this.loaded = true;
      this.name = name;
      this.offset = offset;
      this.isCounter = isCounter;
      this.largerBetter = largerBetter;
      this.typeCode = typeCode;
      this.units = units;
      this.desc = desc;
    }

    public boolean isLoaded() {
      return this.loaded;
    }

    public void unload() {
      this.loaded = false;
      this.name = null;
      this.units = null;
      this.desc = null;
    }

    /**
     * @return the type code of this statistic.
     */
    public byte getTypeCode() {
      return this.typeCode;
    }

    /**
     * @return the name of this statistic.
     */
    public String getName() {
      return this.name;
    }

    /**
     * @return true if this statistic's value will always increase.
     */
    public boolean isCounter() {
      return this.isCounter;
    }

    /**
     * @return true if larger values indicate better performance.
     */
    public boolean isLargerBetter() {
      return this.largerBetter;
    }

    /**
     * @return a string that describes the units this statistic measures.
     */
    public String getUnits() {
      return this.units;
    }

    /**
     * @return a textual description of this statistic.
     */
    public String getDescription() {
      return this.desc;
    }

    /**
     * @return the offset of this stat in its type.
     */
    public int getOffset() {
      return this.offset;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("StatDescriptor [loaded=").append(loaded).append(", name=").append(name).append(", offset=")
				.append(offset).append(", isCounter=").append(isCounter).append(", largerBetter=").append(largerBetter)
				.append(", typeCode=").append(typeCode).append(", units=").append(units).append(", desc=").append(desc)
				.append("]");
		return builder.toString();
	}
    
    
  }