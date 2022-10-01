package com.vmware.data.services.gemfire.operations.functions;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.geode.cache.snapshot.SnapshotFilter;

/**
 * This object will filter out keys for a snapshot
 * @author Gregory Green
 *
 */
public class KeySetSnapshotFilter implements SnapshotFilter<Object, Object>
{
	/**
	 * 
	 * @param keys the keys to filter
	 */
	public KeySetSnapshotFilter(Set<?> keys)
	{
		if(keys == null)
			throw new IllegalArgumentException("Keys required");
		
		this.keys = keys;
	}// --------------------------------------------------------
	@Override
	public boolean accept(Entry<Object, Object> entry)
	{
		
		return keys != null && keys.contains(entry.getKey());
	}// --------------------------------------------------------
	
	private final Set<?> keys;
	private static final long serialVersionUID = 1353360360232497247L;
}
