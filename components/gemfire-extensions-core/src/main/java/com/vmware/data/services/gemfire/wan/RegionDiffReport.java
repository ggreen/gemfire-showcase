package com.vmware.data.services.gemfire.wan;

import java.util.Set;

/**
 * 
 * @author Gregory Green
 *
 */
public class RegionDiffReport {

	private String regionName;
	
	//these are keys that are on the source but not the target
	private Set<?> keysMissingOnTarget;
	
	// these are keys that have different values on source and target
	private Set<?> keysDifferentOnTarget;
	
	//these are keys that are on the target but not the source
	//i.e. keys that should be removed from the target
	private Set<?> keysRemovedFromSource;
	
	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Set<?> getKeysMissingOnTarget() {
		return keysMissingOnTarget;
	}

	public void setKeysMissingOnTarget(Set<?> keysMissingOnTarget) {
		this.keysMissingOnTarget = keysMissingOnTarget;
		
		if(keysMissingOnTarget != null && !keysMissingOnTarget.isEmpty())
			this.different = true;
	}

	public Set<?> getKeysDifferentOnTarget() {
		return keysDifferentOnTarget;
	}

	public void setKeysDifferentOnTarget(Set<?> keysDifferentOnTarget) {
		this.keysDifferentOnTarget = keysDifferentOnTarget;
		
		if(keysDifferentOnTarget != null && !keysDifferentOnTarget.isEmpty())
			this.different = true;
	}

	public Set<?> getKeysRemovedFromSource() {
		return keysRemovedFromSource;
	}

	public void setKeysRemovedFromSource(Set<?> keysRemovedFromSource) {
		this.keysRemovedFromSource = keysRemovedFromSource;
		
		
		if(keysRemovedFromSource != null && !keysRemovedFromSource.isEmpty())
			this.different = true;
	}

	/**
	 * @return the different
	 */
	public boolean isDifferent()
	{
		return different;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("RegionSyncReport [regionName=").append(regionName)
				.append(", keysMissingOnTarget=").append(keysMissingOnTarget)
				.append(", keysDifferentOnTarget=")
				.append(keysDifferentOnTarget)
				.append(", keysRemovedFromSource=")
				.append(keysRemovedFromSource).append(", different=")
				.append(different).append("]");
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
		result = prime
				* result
				+ ((keysDifferentOnTarget == null) ? 0 : keysDifferentOnTarget
						.hashCode());
		result = prime
				* result
				+ ((keysMissingOnTarget == null) ? 0 : keysMissingOnTarget
						.hashCode());
		result = prime
				* result
				+ ((keysRemovedFromSource == null) ? 0 : keysRemovedFromSource
						.hashCode());
		result = prime * result
				+ ((regionName == null) ? 0 : regionName.hashCode());
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
		RegionDiffReport other = (RegionDiffReport) obj;
		if (different != other.different)
			return false;
		if (keysDifferentOnTarget == null)
		{
			if (other.keysDifferentOnTarget != null)
				return false;
		}
		else if (!keysDifferentOnTarget.equals(other.keysDifferentOnTarget))
			return false;
		if (keysMissingOnTarget == null)
		{
			if (other.keysMissingOnTarget != null)
				return false;
		}
		else if (!keysMissingOnTarget.equals(other.keysMissingOnTarget))
			return false;
		if (keysRemovedFromSource == null)
		{
			if (other.keysRemovedFromSource != null)
				return false;
		}
		else if (!keysRemovedFromSource.equals(other.keysRemovedFromSource))
			return false;
		if (regionName == null)
		{
			if (other.regionName != null)
				return false;
		}
		else if (!regionName.equals(other.regionName))
			return false;
		return true;
	}
	
	private boolean different = false;

	
}
