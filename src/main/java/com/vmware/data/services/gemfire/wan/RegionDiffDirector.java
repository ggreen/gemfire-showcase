package com.vmware.data.services.gemfire.wan;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Director for building the RegionSyncReport object
 * @author Gregory Green
 *
 */
public class RegionDiffDirector
{
	/**
	 * 
	 * @param regionName the region to build the report for
	 */
	public RegionDiffDirector(String regionName)
	{
		this.regionName = regionName;
	}// --------------------------------------------------------
	/**
	 * Build the  RegionSyncReport the given 
	 * @param sourceChecksumMap the source map with the key and entry checksum
	 * @param targetMap the target map
	 */
	public void constructComparison(Map<?,BigInteger> sourceChecksumMap, Map<?,BigInteger> targetMap)
	{
		if(sourceChecksumMap == null)
		{
			if(targetMap != null && !targetMap.isEmpty())
			{
				this.keysRemovedFromSource.addAll(targetMap.keySet());
			}
			
			return;
		}
		
		if(targetMap == null)
		{
			this.keysMissingOnTarget.addAll(sourceChecksumMap.keySet());
			
			return;
		}
		
		BigInteger targetBi = null;
		BigInteger sourceBi = null;
		for (Map.Entry<?, BigInteger> entrySource : sourceChecksumMap.entrySet())
		{
			
			targetBi = targetMap.get(entrySource.getKey());
			sourceBi = sourceChecksumMap.get(entrySource.getKey());
			
			if(targetBi == null)
			{
				keysMissingOnTarget.add(entrySource.getKey());
			}
			else if(!targetBi.equals(sourceBi))
			{
				keysDifferentOnTarget.add(entrySource.getKey());
			}
		}
		
		//determine keysRemovedFromSource
		Set<?> sourceKeySet = sourceChecksumMap.keySet();
		for (Map.Entry<?, ?> targetEntry : targetMap.entrySet())
		{
			if(!sourceKeySet.contains(targetEntry.getKey()))
			{
				keysRemovedFromSource.add(targetEntry.getKey());
			}
		}
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @return build the region synchronization report
	 */
	public RegionDiffReport getRegionSyncReport()
	{
		RegionDiffReport regionSyncReport = new RegionDiffReport();
		
		
		regionSyncReport.setKeysDifferentOnTarget(keysDifferentOnTarget);
		regionSyncReport.setKeysMissingOnTarget(keysMissingOnTarget);
		
		regionSyncReport.setKeysRemovedFromSource(keysRemovedFromSource);
		
		regionSyncReport.setRegionName(regionName);
		
		return regionSyncReport;
	}// --------------------------------------------------------
	/**
	 * Clear all built sync report information
	 */
	public void clear()
	{
		keysDifferentOnTarget.clear();
		keysMissingOnTarget.clear();
		keysRemovedFromSource.clear();
	}
	private final String regionName;
	private final Set<Object> keysDifferentOnTarget = new HashSet<Object>();
	private Set<Object> keysMissingOnTarget = new HashSet<Object>();
	private Set<Object> keysRemovedFromSource = new HashSet<Object>();
}
