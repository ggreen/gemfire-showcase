package com.vmware.data.services.apache.geode.lucene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.geode.cache.Region;

import com.vmware.data.services.apache.geode.io.Querier;
import nyla.solutions.core.util.BeanComparator;
import nyla.solutions.core.util.Organizer;

public class GeodePagination
{
	/**
	 * 
	 * @param id the unique ID
	 * @param pageNumber the page number
	 * @param pageRegion the page region
	 * @return the collection
	 */
	public <K> Collection<K> readKeys(String id, int pageNumber ,Region<String,Collection<?>> pageRegion)
	{
		String pageKey = toPageKey(id, pageNumber);
		
		return (Collection)pageRegion.get(pageKey);
	}// --------------------------------------------------------------
	
	
	/**
	 * Store the pagination search result details
	 * @param <K> the key class
	 * @param <V> the value class
	 * @param id the unique id
	 * @param pageSize the page size
	 * @param pageKeysRegion the region where pages keys are stors
	 * @param results the collection os results
	 * @return the list of keys in the page region
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <K, V> List<String> storePaginationMap(String id,int pageSize,
			Region<String, Collection<K>> pageKeysRegion,
			List<Map.Entry<K, V>> results)
	{
		if(results == null || results.isEmpty())
			return null;
		
		//add to pages
		 List<Collection<K>> pagesCollection = toKeyPages((List)results, pageSize);

		 int pageIndex = 1;
		 String key = null;
		 ArrayList<String> keys = new ArrayList<String>(pageSize);
		 for (Collection<K> page : pagesCollection)
		{
			 //store in region
			 key = toPageKey(id,pageIndex++);
			 
			 pageKeysRegion.put(key, page);
			 
			 keys.add(key);
		}
		 
		 keys.trimToSize();
		return keys;
	}// --------------------------------------------------------------
	/***
	 * 
	 * @param id the ID
	 * @param pageNumber the page number
	 * @return the id-pageNumber
	 */
	public static String toPageKey(String id,int pageNumber)
	{
		return new StringBuilder().append(id).append("-").append(pageNumber).toString();
	}// --------------------------------------------------------------
	
	/**
	 * @param <K> the key class
	 * @param <V> the value class
	 * @param mapEntries the map entries
	 * @param pageSize the page size
	 * @return the collection of key
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K,V> List<Collection<K>> toKeyPages(List<Map.Entry<K, V>> mapEntries,int pageSize)
	{
		if(mapEntries == null || mapEntries.isEmpty())
			return null;
				
		int collectionSize = mapEntries.size();
		
		if(pageSize <= 0 || collectionSize <= pageSize)
		{
			ArrayList<K> list = new ArrayList<K>(mapEntries.size());
			for (Map.Entry<K, V> entry : mapEntries)
			{
				if(entry == null)
					continue;
				
				list.add(entry.getKey());
			}
			
			if(list.isEmpty())
				return null;
			
			return Collections.singletonList(list);
		}
	
		int initialSize = collectionSize /pageSize;
		
		ArrayList<Collection<K>> list = new ArrayList(initialSize);
		
		ArrayList<K> current = new ArrayList<K>();
		for (Map.Entry<K, V> entry : mapEntries)
		{
			current.add(entry.getKey());
			
			if(current.size() >= pageSize)
			{
				current.trimToSize();
				
				list.add((Collection<K>)current);
				current = new ArrayList<K>();
			}
		}
		
		if(!current.isEmpty())
			list.add((Collection<K>)current);
		
		return (List<Collection<K>>)list;
	
	}//------------------------------------------------
	public List<String> storePagination(String sanId, int pageSize,
			Map<String, Collection<?>> pageKeysRegion,
			Collection<String> keys)
	{
		if(keys == null || keys.isEmpty())
			return null;
		
		//add to pages
		 List<Collection<String>> pagesCollection = Organizer.toPages(keys, pageSize);

	
		 String key = null;
		 int pageIndex = 1;
		 ArrayList<String> pageKeys = new ArrayList<String>(10);
		 for (Collection<String> page : pagesCollection)
		{
			 //store in region
			 key = new StringBuilder().append(sanId).append("-").append(pageIndex++).toString();
			 
			 pageKeysRegion.put(key, page);
			 
			 pageKeys.add(key);
		}
		 
		 pageKeys.trimToSize();
		return pageKeys;
	}
	/**
	 * Read Results from region by keys in pageRegion
	 * @param <K> the key class
	 * @param <V> the value class
	 * @param criteria the text page criteria
	 * @param pageNumber the page number to retrieve
	 * @param region the region where data is retrieved
	 * @param pageRegion the page region with key contains the pageKey and value the keys to the region
	 * @return region.getAll(pageKey)
	 */
	public <K,V> Map<K,V> readResultsByPage(TextPageCriteria criteria, int pageNumber, Region<K,V> region, Region<String,Collection<?>> pageRegion)
	{
		if(pageRegion == null )
			return null;
		
		Collection<?> regionKeys = pageRegion.get(criteria.toPageKey(pageNumber));
		
		if(regionKeys == null|| regionKeys.isEmpty())
			return null;
		
		return region.getAll(regionKeys);
	}
	
	@SuppressWarnings("unchecked")
	public <K,V> Collection<V> readResultsByPageValues(String criteriaId, String sortField, boolean desc,int pageNumber, Region<K,V> region, Region<String,Collection<?>> pageRegion)
	{
		if(pageRegion == null )
			return null;
		
		Collection<K> regionKeys = (Collection<K>)pageRegion.get(criteriaId+"-"+pageNumber);
		
		if(regionKeys == null|| regionKeys.isEmpty())
			return null;
		
		if(sortField != null)
		{
			String field = sortField.replace("entry.", "");
			BeanComparator c = new BeanComparator(field,desc);
			
			TreeSet<V> set = new TreeSet<V>(c);
			
			for (K key : regionKeys)
			{
				set.add(region.get(key));
			}
			
			return set;
		}
		else
		{
			
			ArrayList<V> list = new ArrayList<V>();
			for (K key : regionKeys)
			{
				list.add(region.get(key));
			}
			
			return list;
			
		}
	}//------------------------------------------------
	public <K> Collection<String> clearSearchResultsByPage(TextPageCriteria criteria, Region<String,Collection<K>> pageRegion)
	{		
		///TODO: Get previous page numbers faster (without a query)
		Collection<String> pageKeys = Querier.query("select * from /"+criteria.getPageRegionName()+".keySet() k where k like '"+criteria.getId()+"%'");
		
		if(pageKeys == null || pageKeys.isEmpty())
			return null;
		
		pageRegion.removeAll(pageKeys);
		
		return pageKeys;
	}//------------------------------------------------
}