package com.vmware.data.services.gemfire.lucene.function;

import com.vmware.data.services.gemfire.lucene.TextPageCriteria;
import com.vmware.data.services.gemfire.lucene.TextPageCriteriaBuilder;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.lucene.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;


public class LuceneSearchFunction<T> implements Function<Object>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Supplier<Cache> cacheSupplier;
	private final Supplier<LuceneService> luceneServiceSupplier;
	private Logger logger = LogManager.getLogger(LuceneSearchFunction.class);

	public LuceneSearchFunction()
	{
		this( () -> CacheFactory.getAnyInstance(),
				() -> LuceneServiceProvider.get(CacheFactory.getAnyInstance()));
	}

	public LuceneSearchFunction(Supplier<Cache> cacheSupplier, Supplier<LuceneService> luceneServiceSupplier) {
		this.cacheSupplier = cacheSupplier;
		this.luceneServiceSupplier = luceneServiceSupplier;
	}


	@Override
	public String getId() {
		return "LuceneSearchFunction";
	}
	/**
	 * Execute the search on Region
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(FunctionContext functionContext) 
	{
		Cache cache = cacheSupplier.get();
		
		try
		{
			//Function must be executed on Region
			if(!(functionContext instanceof RegionFunctionContext))
			{	
				throw new FunctionException("Execute on a region");
			}
			
			Object args = functionContext.getArguments();
			
			if (args == null)
				throw new FunctionException("arguments is required");
			
			TextPageCriteria criteria = new TextPageCriteriaBuilder().args(args).build();

			logger.info("criteria: {}",criteria);

			Region<String, Collection<Object>> pagingRegion = cache.getRegion(criteria.getPageRegionName());
			
			Region<?,?> region = cache.getRegion(criteria.getRegionName());

			LuceneService luceneService = luceneServiceSupplier.get();

			LuceneQuery<Object, Object> query = luceneService.createLuceneQueryFactory()
					.setLimit(criteria.getLimit())
					.setPageSize(criteria.getSize())
					.create(criteria.getIndexName(),
							criteria.getRegionName(),
							criteria.getQuery(),
							criteria.getDefaultField());

			if (criteria.getKeysOnly()) {
					logger.info("Returning keys only");
                    functionContext.getResultSender().lastResult(query.findKeys());
            }
			else {

				logger.info("Returning pages");
				PageableLuceneQueryResults<Object, Object> pageableLuceneQueryResults = query.findPages();

				int pageNumber = 1;

				List firstPage = null;
				List page = null;

				while (pageableLuceneQueryResults.hasNext()) {
					page = pageableLuceneQueryResults.next();

					if(firstPage ==null){
						firstPage = page;
						functionContext.getResultSender().lastResult(firstPage);
					}

					logger.info("Returning paging");
					pagingRegion.put(toPageKey(criteria.getId(), pageNumber),page);
				}


			}
			
//			GemFirePagination pagination = new GemFirePagination();

//			TextPolicySearchStrategy geodeSearch = new TextPolicySearchStrategy(this.luceneServiceSupplier);


//			geodeSearch.saveSearchResultsWithPageKeys(criteria, criteria.getQuery(),null, (Region<String,Collection<Object>>)pagingRegion);
			
			//build results
//			Collection<Object> collection = pagination.readResultsByPageValues(criteria.getId(),criteria.getSortField(),
//			criteria.isSortDescending(),
//					criteria.getBeginIndex(),
//					(Region<Object,Object>)region, (Region)pagingRegion);
//
//			if(collection == null)
//			{
//				functionContext.getResultSender().lastResult(null);
//				return;
//
//			}
//
//			PagingCollection<Object> pageCollection = new PagingCollection<Object>(collection, criteria);
//			functionContext.getResultSender().lastResult(collection);
		}
		catch (LuceneQueryException luceneQueryException) {
			throw new FunctionException(luceneQueryException);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public static String toPageKey(String id,int pageNumber)
	{
		return new StringBuilder().append(id).append("-").append(pageNumber).toString();
	}
}
