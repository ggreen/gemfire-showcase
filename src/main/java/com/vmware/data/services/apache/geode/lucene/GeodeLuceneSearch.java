package com.vmware.data.services.apache.geode.lucene;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

import com.vmware.data.services.apache.geode.client.GeodeClient;
import com.vmware.data.services.apache.geode.io.GemFireIO;
import com.vmware.data.services.apache.geode.io.Querier;
import com.vmware.data.services.apache.geode.lucene.function.SimpleLuceneSearchFunction;
import nyla.solutions.core.data.MapEntry;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.util.BeanComparator;
import nyla.solutions.core.util.Organizer;

/**
 * Implement for searching Lucene
 * @author Gregory Green
 *
 */
public class GeodeLuceneSearch
{
	private final LuceneService luceneService;
	/**
	 * 
	 * @param gemFireCache the cache
	 */
	public GeodeLuceneSearch(GemFireCache gemFireCache)
	{
		this(LuceneServiceProvider.get(gemFireCache));
	}//------------------------------------------------
	/**
	 * 
	 * @param luceneService the luceneService
	 */
	public GeodeLuceneSearch(LuceneService luceneService)
	{
		this.luceneService = luceneService;
	}//------------------------------------------------

	public Collection<String>  saveSearchResultsWithPageKeys(TextPageCriteria criteria, Region<String,Collection<?>> pageKeysRegion)
	{
		if(criteria == null)
			return null;
		
		if(criteria.getQuery() == null || criteria.getQuery().length() == 0)
			return null;
		
		if(criteria.getIndexName() == null || criteria.getIndexName().length() == 0)
			throw new IllegalArgumentException("Default criteria's indexName is required");
		
		if(criteria.getId() == null || criteria.getId().length() == 0)
			throw new IllegalArgumentException("Default criteria's id is required");
		
		if(criteria.getDefaultField() == null || criteria.getDefaultField().length() == 0)
			throw new IllegalArgumentException("Default criteria's defaultField is required");
			
		try
		{	
			
			LuceneQuery<Object, Object> luceneQuery = luceneService.createLuceneQueryFactory()
			  .create(criteria.getIndexName(), 
			  criteria.getRegionName(), 
			  criteria.getQuery(), criteria.getDefaultField());
			
			 List<LuceneResultStruct<Object, Object>> list = luceneQuery.findResults();
			 
			 luceneQuery.findPages();
			 
			 if(list == null || list.isEmpty())
				 return null;
			
			 String sortField =  criteria.getSortField();
			 BeanComparator beanComparator = null;
			 
			 Collection<Map.Entry<Object,Object>> results = null;
			 if(sortField != null && sortField.trim().length() > 0 )
			 {
				 beanComparator = new BeanComparator(sortField,criteria.isSortDescending());
				 
				 Collection<Map.Entry<Object,Object>> set = new TreeSet<Map.Entry<Object,Object>>(beanComparator);
				 list.parallelStream().forEach(e -> set.add(new MapEntry<Object,Object>(e.getKey(), e.getValue())));
				 results = set;
			 }
			 else
			 {
				 results = list.stream().map( e -> new MapEntry<>(e.getKey(), e.getValue())).collect(Collectors.toList());
			 }
			
			 
			 //add to pages
			 List<Collection<Object>> pagesCollection = Organizer.toKeyPages(results, criteria.getEndIndex() - criteria.getBeginIndex());
		 
			 int pageIndex = 0;
			 String key = null;
			 ArrayList<String> keys = new ArrayList<String>(10);
			 for (Collection<Object> page : pagesCollection)
			{
				 //store in region
				 key = new StringBuilder().append(criteria.getId()).append("-").append(pageIndex++).toString();
				 
				 pageKeysRegion.put(key, page);
				 
				 keys.add(key);
			}
			 
			 keys.trimToSize();
			 
			 return keys;
	
		}
		catch (LuceneQueryException e)
		{
			throw new SystemException(e);
		}		
	}//------------------------------------------------
	/**
	 * Read Results from region by keys in pageRegion
	 * @param <K> the key type
	 * @param <V> the value type
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
	public <K,V> Collection<V> readResultsByPageValues(TextPageCriteria criteria, int pageNumber, Region<K,V> region, Region<String,Collection<?>> pageRegion)
	{
		if(pageRegion == null )
			return null;
		
		Collection<K> regionKeys = (Collection<K>)pageRegion.get(criteria.toPageKey(pageNumber));
		
		if(regionKeys == null|| regionKeys.isEmpty())
			return null;
		
		if(criteria.getSortField() != null)
		{
			String field = criteria.getSortField().replace("entry.", "");
			BeanComparator c = new BeanComparator(field);
			
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
	public Collection<String> clearSearchResultsByPage(TextPageCriteria criteria, Region<String,Collection<?>> pageRegion)
	{		
		Collection<String> pageKeys = Querier.query("select * from /"+criteria.getPageRegionName()+".keySet() k where k like '"+criteria.getId()+"%'");
		
		
		pageRegion.removeAll(pageKeys);
		
		return pageKeys;
	}//------------------------------------------------
	@SuppressWarnings("unchecked")
	public <T> Collection<T> search(String indexName,String regionName,String queryString,String defaultField) 
	throws Exception
	{
		Region<?,?> region = GeodeClient.connect().getRegion(regionName);
		
		String[] args = {indexName,regionName,queryString,defaultField};
	
		return GemFireIO.exeWithResults(FunctionService.onRegion(region).setArguments(args).setArguments(args), new SimpleLuceneSearchFunction());
	}
	


	

}
