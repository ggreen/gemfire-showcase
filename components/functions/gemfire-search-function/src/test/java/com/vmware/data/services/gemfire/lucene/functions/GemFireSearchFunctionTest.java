package com.vmware.data.services.gemfire.lucene.functions;

import com.vmware.data.services.gemfire.lucene.functions.domain.SearchCriteria;
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

import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GemFireSearchFunctionTest {

    private GemFireSearchFunction subject;

    @Mock
    private RegionFunctionContext<Object> fc;


    @Mock
    private Cache cache;

    @Mock
    private SearchCriteria searchCriteria;

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
    private static final String query = "query";
    private static final String indexName = "index";

    private static final String defaultField = "field";
    private static final String limit = "100";
    private static final String id = "id";
    private static final String regionName = "myRegion";
    private String[] stringArgs;
    private static final int pageSize = 2;


    @BeforeEach
    void setUp() {

        stringArgs = new String[]{id, indexName, defaultField, query, limit};

        subject = new GemFireSearchFunction(() -> cache, () -> luceneService);
    }

    @Test
    void execute() throws LuceneQueryException {

        when(fc.getArguments()).thenReturn(stringArgs);
        when(fc.getDataSet()).thenReturn(region);
        when(region.getName()).thenReturn(regionName);
        when(luceneService.createLuceneQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.create(anyString(), anyString(), anyString(), anyString())).thenReturn(queryObject);
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

    @Test
    void execute_keysOnly() throws LuceneQueryException {

        String[] keysOnlyArgs = new String[]{id,
                indexName,
                defaultField,
                query,
                limit, valueOf(pageSize),
                Boolean.TRUE.toString()};

        when(fc.getArguments()).thenReturn(keysOnlyArgs);
        when(fc.getDataSet()).thenReturn(region);
        when(region.getName()).thenReturn(regionName);
        when(luceneService.createLuceneQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.create(anyString(), anyString(), anyString(), anyString())).thenReturn(queryObject);
        when(queryFactory.setPageSize(anyInt())).thenReturn(queryFactory);
        when(queryFactory.setLimit(anyInt())).thenReturn(queryFactory);
        when(fc.getResultSender()).thenReturn(rs);
        when(cache.getRegion(anyString())).thenReturn(pagingRegion)
                .thenReturn(region);

        subject.execute(fc);

        verify(queryObject).findKeys();

        verify(rs).lastResult(any());

    }

}