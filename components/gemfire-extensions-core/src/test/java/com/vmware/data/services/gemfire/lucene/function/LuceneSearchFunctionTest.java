package com.vmware.data.services.gemfire.lucene.function;

import com.vmware.data.services.gemfire.lucene.TextPageCriteria;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LuceneSearchFunctionTest {

    private LuceneSearchFunction subject;

    @Mock
    private RegionFunctionContext<Object> fc;


    @Mock
    private Cache cache;

    @Mock
    private TextPageCriteria textPageCriteria;

    @Mock
    private LuceneService luceneService;

    @Mock
    private LuceneQueryFactory queryFactory;

    @Mock
    private LuceneQuery<Object, Object> queryObject;

    @Mock
    private PageableLuceneQueryResults<Object, Object> pages;

    @Mock
    private ResultSender<Object> rs;

    @Mock
    private Region<Object, Object> pagingRegion;

    @Mock
    private Region<Object, Object> region;


    @BeforeEach
    void setUp() {
        subject= new LuceneSearchFunction(() -> cache, ()-> luceneService);
    }

    @Test
    void execute() throws LuceneQueryException {

        String query = "query";
        String regionName = "region";
        String indexName = "index";

        String defaultField = "field";
        String limit = "100";
        String id = "id";
        String[] stringArgs = {id,query,regionName,indexName,defaultField,limit};

        when(fc.getArguments()).thenReturn(stringArgs);
        when(luceneService.createLuceneQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.create(anyString(),anyString(),anyString(),anyString())).thenReturn(queryObject);
        when(queryFactory.setPageSize(anyInt())).thenReturn(queryFactory);
        when(queryFactory.setLimit(anyInt())).thenReturn(queryFactory);
        when(queryObject.findPages()).thenReturn(pages);
        when(pages.hasNext()).thenReturn(true).thenReturn(false);
        when(fc.getResultSender()).thenReturn(rs);
        when(cache.getRegion(anyString())).thenReturn(pagingRegion)
                        .thenReturn(region);

        subject.execute(fc);

        verify(rs).lastResult(any());


    }
}