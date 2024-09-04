package com.vmware.data.services.gemfire.io.search;

import static org.junit.jupiter.api.Assertions.*;

import com.vmware.data.services.gemfire.lucene.GemFireLuceneSearch;
import com.vmware.data.services.gemfire.lucene.TextPageCriteria;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class GemFireLuceneSearchTest
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	//@Test
	public void testSearchWithPageKeys()
	throws Exception
	{
		Region pageRegion = Mockito.mock(Region.class);
		
		LuceneService luceneService = mock(LuceneService.class);
		
		LuceneQueryFactory factory = mock(LuceneQueryFactory.class);
		LuceneQuery<Object, Object> query = mock(LuceneQuery.class);
		
		when(luceneService.createLuceneQueryFactory()).thenReturn(factory);
		
		when(factory.create(any(), any(), any(),any())).thenReturn(query);
		
		ArrayList<LuceneResultStruct<Object, Object>> results = new ArrayList<LuceneResultStruct<Object, Object>>();
		
		LuceneResultStruct<Object, Object> luceneResultStruct = mock(LuceneResultStruct.class);
		
		results.add(luceneResultStruct);
		
		when(query.findResults()).thenReturn(results);
		
		GemFireLuceneSearch searcher = new GemFireLuceneSearch(luceneService);
		
		
		assertNull(searcher.saveSearchResultsWithPageKeys(null,pageRegion));
		
		TextPageCriteria criteria = TextPageCriteria.builder().id("test").query("test")
				.regionName("region").indexName("index").defaultField("field").limit(100).build();
		assertNull(searcher.saveSearchResultsWithPageKeys(criteria,pageRegion));
		
		assertNotNull(searcher.saveSearchResultsWithPageKeys(criteria,pageRegion));
		
	}

}
