package com.vmware.data.services.apache.geode.lucene.function;

import com.vmware.data.services.apache.geode.lucene.GeodePagination;
import com.vmware.data.services.apache.geode.lucene.TextPageCriteria;
import com.vmware.data.services.apache.geode.lucene.TextPolicySearchStrategy;
import nyla.solutions.core.patterns.iteration.PagingCollection;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxSerializationException;

import java.util.Collection;


public class LuceneSearchFunction<T> implements Function<Object>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	public String getId() {
		return "LuceneSearchFunction";
	}// --------------------------------------------------------------
	/**
	 * Execute the search on Region
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(FunctionContext functionContext) 
	{
		Cache cache = CacheFactory.getAnyInstance();	
		
		try
		{
			//Function must be executed on REgion
			if(!(functionContext instanceof RegionFunctionContext))
			{	
				throw new FunctionException("Execute on a region");
			}
			
			Object args = functionContext.getArguments();
			
			if (args == null)
				throw new FunctionException("arguments is required");
			
			TextPageCriteria criteria = null;
			
			if(args instanceof PdxInstance)
			{
				PdxInstance  pdxInstance = (PdxInstance)args;
				
				try
				{
					criteria = (TextPageCriteria)(pdxInstance.getObject());
				}
				catch (PdxSerializationException e)
				{
					throw new FunctionException(e.getMessage()+" JSON:"+JSONFormatter.toJSON(pdxInstance));
				}
			}
			else 
			{
				criteria = (TextPageCriteria)args;
			}
			
				
			Region<String, Collection<Object>> pagingRegion = cache.getRegion(criteria.getPageRegionName());
			
			Region<?,?> region = cache.getRegion(criteria.getRegionName());
			
			GeodePagination pagination = new GeodePagination();
			
			TextPolicySearchStrategy geodeSearch = new TextPolicySearchStrategy(cache);
			
			//Collection<String> keys =  (Collection<String>)checkCachedKeysByCriteria(criteria,searchRequest,pagination,pagingRegion);
				
				
				
				
			geodeSearch.saveSearchResultsWithPageKeys(criteria, criteria.getQuery(),null, (Region<String,Collection<Object>>)pagingRegion);
			
			//build results
			Collection<Object> collection = pagination.readResultsByPageValues(criteria.getId(),criteria.getSortField(), 
			criteria.isSortDescending(),
					criteria.getBeginIndex(), 
					(Region<Object,Object>)region, (Region)pagingRegion);
			
			if(collection == null)
			{
				functionContext.getResultSender().lastResult(null);
				return;
				
			}
			
			
			PagingCollection<Object> pageCollection = new PagingCollection<Object>(collection, criteria);
			

			functionContext.getResultSender().lastResult(pageCollection);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}// --------------------------------------------------------------
	

}
