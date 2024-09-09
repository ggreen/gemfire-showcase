package com.vmware.data.services.gemfire.lucene.functions;

import com.vmware.data.services.gemfire.lucene.functions.domain.SearchCriteria;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GemFireSearchFunctionTest {

    private GemFireSearchFunction subject;

    @Mock
    private FunctionContext<Object> fc;

    @Mock
    private RegionFunctionContext<Object> rfc;


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
    void mustExecuteOnServer() throws LuceneQueryException {

        try {
            subject.execute(rfc);
            fail();
        }
        catch (FunctionException e){
            assertThat(e.getMessage()).contains("onServer");
        }

    }

    @Test
    void execute() throws LuceneQueryException {

        when(fc.getArguments()).thenReturn(stringArgs);
        when(region.getName()).thenReturn(regionName);
        when(luceneService.createLuceneQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.create(anyString(), anyString(), anyString(), anyString())).thenReturn(queryObject);
        when(queryFactory.setPageSize(anyInt())).thenReturn(queryFactory);
        when(queryFactory.setLimit(anyInt())).thenReturn(queryFactory);
        when(queryObject.findPages()).thenReturn(pages);
        when(pages.hasNext()).thenReturn(true).thenReturn(false);
        when(fc.getResultSender()).thenReturn(rs);
        when(cache.getRegion(anyString()))
                .thenReturn(region).thenReturn(pagingRegion);

        subject.execute(fc);

        verify(rs).lastResult(any());

    }

    @Test
    void execute_keysOnly() throws LuceneQueryException {

        String[] keysOnlyArgs = new String[]{id,
                regionName,
                indexName,
                defaultField,
                query,
                limit, valueOf(pageSize),
                Boolean.TRUE.toString()};

        when(fc.getArguments()).thenReturn(keysOnlyArgs);
        when(region.getName()).thenReturn(regionName);
        when(luceneService.createLuceneQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.create(anyString(), anyString(), anyString(), anyString())).thenReturn(queryObject);
        when(queryFactory.setPageSize(anyInt())).thenReturn(queryFactory);
        when(queryFactory.setLimit(anyInt())).thenReturn(queryFactory);
        when(fc.getResultSender()).thenReturn(rs);
        when(cache.getRegion(anyString()))
                .thenReturn(region).thenReturn(pagingRegion);

        subject.execute(fc);

        verify(queryObject).findKeys();

        verify(rs).lastResult(any());

    }

    @Test
    void regionDoesNotExist() {
        String[] invalidRegion = new String[]{id,
                "NoRegions",
                indexName,
                defaultField,
                query,
                limit, valueOf(pageSize),
                Boolean.TRUE.toString()};

        when(fc.getArguments()).thenReturn(invalidRegion);
        when(cache.getRegion(anyString())).thenReturn(null);

        try {
            subject.execute(fc);
        }
        catch (FunctionException e){
            assertThat(e.getMessage()).contains("does not exist");
        }

    }


    @Test
    void pagingRegionDoesNotExist() {
        String[] invalidRegion = new String[]{id,
                regionName,
                indexName,
                defaultField,
                query,
                limit, valueOf(pageSize),
                Boolean.TRUE.toString()};

        when(fc.getArguments()).thenReturn(invalidRegion);
        when(cache.getRegion(anyString())).thenReturn(null).thenReturn(region);

        try {
            subject.execute(fc);
        }
        catch (FunctionException e){
            assertThat(e.getMessage()).contains("does not exist");
        }

    }
}