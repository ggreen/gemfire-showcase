package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vmware.data.services.apache.geode.operations.stats.GfStatsReader;
import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;

/*
 * Defines a statistic resource type. Each resource instance must be of a
 * single type. The type defines what statistics each instance of it will
 * support. The type also has a description of itself.
 */
public class ResourceType implements StatsInfo
{
	  private boolean loaded;
	    //    private final int id;
	    private final String name;
	    private String desc;
	    private final StatDescriptor[] stats;
	    private Map<String,StatDescriptor> descriptorMap;

	    public void dump(PrintWriter stream) {
	      if (loaded) {
	        stream.println(name + ": " + desc);
	        for (StatDescriptor stat : stats) {
	          stat.dump(stream);
	        }
	      }
	    }

	    public ResourceType(String name, int statCount) {
	      this.loaded = false;
	      this.name = name;
	      this.desc = null;
	      this.stats = new StatDescriptor[statCount];
	      this.descriptorMap = null;
	    }

	    public ResourceType(String name, String desc, int statCount) {
	      this.loaded = true;
	      this.name = name;
	      this.desc = desc;
	      this.stats = new StatDescriptor[statCount];
	      this.descriptorMap = new HashMap<String,StatDescriptor>();
	    }
	    
	    public boolean isRegion()
	    {
	    	return name != null && name.contains("PartitionedRegionStats");
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
	        for (int i = 0; i < stats.length; i++) {
	          if (stats[i] != null) {
	            if (!stats[i].isLoaded()) {
	              stats[i] = null;
	            }
	          }
	        }
	        return false;
	      } else {
	        return true;
	      }
	    }

	    void unload() {
	      this.loaded = false;
	      this.desc = null;
	      for (StatDescriptor stat : this.stats) {
	        stat.unload();
	      }
	      this.descriptorMap.clear();
	      this.descriptorMap = null;
	    }

	    public void addStatDescriptor(GfStatsReader archive, int offset,
                                      String name, boolean isCounter,
                                      boolean largerBetter,
                                      byte typeCode, String units, String desc) {
	      StatDescriptor descriptor = new StatDescriptor(name, offset, isCounter,
	          largerBetter, typeCode, units, desc);
	      this.stats[offset] = descriptor;
	      if (archive.loadStatDescriptor(descriptor, this)) {
	        descriptorMap.put(name, descriptor);
	      }
	    }

//	    private int getId() {
//	      return this.id;
//	    }

	    /*
	     * Returns the name of this resource type.
	     */
	    public String getName() {
	      return this.name;
	    }

	    /*
	     * Returns an array of descriptors for each statistic this resource type
	     * supports.
	     */
	    public StatDescriptor[] getStats() {
	      return this.stats;
	    }

	    /*
	     * Gets a stat descriptor contained in this type given the stats name.
	     *
	     * @param name the name of the stat to find in the current type
	     * @return the descriptor that matches the name or null if the type does not
	     * have a stat of the given name
	     */
	    public StatDescriptor getStat(String name) {
	      return (StatDescriptor) descriptorMap.get(name);
	    }

	    /*
	     * Returns a description of this resource type.
	     */
	    public String getDescription() {
	      return this.desc;
	    }

	    @Override
	    public int hashCode() {
	      final int prime = 31;
	      int result = 1;
	      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	      ResourceType other = (ResourceType) obj;
	      if (name == null) {
	        if (other.name != null)
	          return false;
	      } else if (!name.equals(other.name))
	        return false;
	      return true;
	    }

	    
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("ResourceType [loaded=").append(loaded).append(", name=").append(name).append(", desc=")
					.append(desc).append(", stats=").append(Arrays.toString(stats)).append(", descriptorMap=")
					.append(descriptorMap);
			return builder.toString();
		}

		@Override
		public void accept(StatsVisitor visitor)
		{
			visitor.visitResourceType(this);
			
		}

}
