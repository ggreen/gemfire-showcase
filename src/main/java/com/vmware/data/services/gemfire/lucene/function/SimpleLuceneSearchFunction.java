package com.vmware.data.services.gemfire.lucene.function;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleLuceneSearchFunction implements Function<Object>
{
	private LuceneService luceneService = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5728514774900318029L;
	private Logger log = LogManager.getLogger(SimpleLuceneSearchFunction.class);

	public SimpleLuceneSearchFunction(LuceneService luceneService)
	{
		this.luceneService = luceneService;
	}

	public SimpleLuceneSearchFunction()
	{
	}

	@Override
	public void execute(FunctionContext<Object> context)
	{
		Object args = context.getArguments();
		
		if (args == null)
			throw new IllegalArgumentException("args is required");

		String indexName;
		String regionName;
		String queryString;
		String defaultField;

		if(args instanceof Object[])
		{
			Object[] argsArray = (Object[])args;
			if (argsArray.length < 4)
				throw new IllegalArgumentException("4 Arguments expected");

			indexName = String.valueOf(argsArray[0]);
			regionName = String.valueOf(argsArray[1]);
			queryString= String.valueOf(argsArray[2]);
			defaultField = String.valueOf(argsArray[3]);

			log.info("Inputs {} {} {} {}",indexName,regionName,queryString,defaultField);

		}
		else if (args instanceof String[]){
			String[] inputs =  (String[])args;

			if (inputs.length < 4)
				throw new IllegalArgumentException("4 Arguments expected");

			indexName = inputs[0];
			regionName = inputs[1];
			queryString= inputs[2];
			defaultField = inputs[3];
		}
		else
			throw new FunctionException("Arguments "+String.valueOf(args)+" type "+args.getClass().getName()+" must be of type String[]");


		
		try
		{
			LuceneService ls = getLuceneService();

			LuceneQuery<Object, Object> query = ls.createLuceneQueryFactory().create(indexName, regionName, queryString, defaultField);
			
			context.getResultSender().lastResult(query.findValues());
		}
		catch (LuceneQueryException e)
		{
			e.printStackTrace();
			
			throw new FunctionException(e.getMessage(),e);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			throw new FunctionException(e.getMessage());
		}
		
	}//------------------------------------------------

	private LuceneService getLuceneService()
	{
		if(luceneService != null)
			return luceneService;

		luceneService = LuceneServiceProvider.get(CacheFactory.getAnyInstance());

		return luceneService;
	}

	@Override
	public String getId()
	{
		return "SimpleLuceneSearchFunction";
	}

}
