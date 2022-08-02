package com.vmware.data.services.apache.geode.io.search;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.mockito.Mockito;

import com.vmware.data.services.apache.geode.lucene.GeodeLuceneSearch;
import com.vmware.data.services.apache.geode.lucene.TextPageCriteria;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class GeodeLuceneSearchTest
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
		
		GeodeLuceneSearch searcher = new GeodeLuceneSearch(luceneService);
		
		
		assertNull(searcher.saveSearchResultsWithPageKeys(null,pageRegion));
		
		TextPageCriteria criteria = new TextPageCriteria();
		assertNull(searcher.saveSearchResultsWithPageKeys(criteria,pageRegion));
		
		criteria.setIndexName("TestIndex");
		criteria.setQuery("test");
		criteria.setId("test");
		criteria.setDefaultField("test");
		
		assertNotNull(searcher.saveSearchResultsWithPageKeys(criteria,pageRegion));
		
	}

}
