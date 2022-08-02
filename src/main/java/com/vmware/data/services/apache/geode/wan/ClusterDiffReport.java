package com.vmware.data.services.apache.geode.wan;

import java.util.HashMap;

/**
 * 
 * @author Gregory Green
 *
 */
public class ClusterDiffReport {
	

	public String getSourceLocators() {
		return sourceLocators;
	}

	public void setSourceLocators(String sourceLocators) {
		this.sourceLocators = sourceLocators;
	}

	public String getTargetLocators() {
		return targetLocators;
	}

	public void setTargetLocators(String targetLocators) {
		this.targetLocators = targetLocators;
	}

	public HashMap<String, RegionDiffReport> getRegionReports() {
		return regionReports;
	}

	public void setRegionReports(HashMap<String, RegionDiffReport> regionReports) 
	{
		this.different = false;
		
		this.regionReports = regionReports;
		
		if(this.regionReports != null && !regionReports.isEmpty())
		{
			for (RegionDiffReport regionRegionSyncReport : this.regionReports.values())
			{
				if(regionRegionSyncReport.isDifferent())
					different = true;
				
			}
		}

	}// --------------------------------------------------------

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ClusterSyncReport [sourceLocators=")
				.append(sourceLocators).append(", targetLocators=")
				.append(targetLocators).append(", regionReports=")
				.append(regionReports).append("]");
		return builder.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (different ? 1231 : 1237);
		result = prime * result
				+ ((regionReports == null) ? 0 : regionReports.hashCode());
		result = prime * result
				+ ((sourceLocators == null) ? 0 : sourceLocators.hashCode());
		result = prime * result
				+ ((targetLocators == null) ? 0 : targetLocators.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterDiffReport other = (ClusterDiffReport) obj;
		if (different != other.different)
			return false;
		if (regionReports == null)
		{
			if (other.regionReports != null)
				return false;
		}
		else if (!regionReports.equals(other.regionReports))
			return false;
		if (sourceLocators == null)
		{
			if (other.sourceLocators != null)
				return false;
		}
		else if (!sourceLocators.equals(other.sourceLocators))
			return false;
		if (targetLocators == null)
		{
			if (other.targetLocators != null)
				return false;
		}
		else if (!targetLocators.equals(other.targetLocators))
			return false;
		return true;
	}
	
	
	// "source" means the cluster that contains the correct information
	// the SyncReport describes how the "target" differs from the source

	/**
	 * @return the different
	 */
	public boolean isDifferent()
	{
		return different;
	}


	private boolean different;
	
	private String sourceLocators;
	private String targetLocators;

	private HashMap<String,RegionDiffReport> regionReports;
	
}
