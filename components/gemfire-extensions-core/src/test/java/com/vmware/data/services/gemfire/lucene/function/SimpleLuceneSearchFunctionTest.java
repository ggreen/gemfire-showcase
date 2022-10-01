package com.vmware.data.services.gemfire.lucene.function;

import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SimpleLuceneSearchFunctionTest
{

    private SimpleLuceneSearchFunction subject;
    private FunctionContext<Object> context;
    private String indexName = "index";
    private String regionName = "region";
    private String queryString = "query";
    private String defaultField = "field";
    private String[] validArgs = {indexName,regionName,queryString,defaultField};
    private ResultSender<Object> rs;
    private LuceneService ls;
    private LuceneQueryFactory factory;
    private LuceneQuery<Object, Object> query;

    @BeforeEach
    void setUp()
    {
        query = mock(LuceneQuery.class);
        ls = mock(LuceneService.class);
        factory = mock(LuceneQueryFactory.class);
        context = mock(FunctionContext.class);
        rs = mock(ResultSender.class);
        subject = new SimpleLuceneSearchFunction();
    }

    @Test
    void execute_ThrowsIllegalArgument()
    {
        assertThrows(IllegalArgumentException.class, () -> subject.execute(context));
    }


    @Test
    void execute_ThrowsIllegalArgument_WhenArgsLength()
    {
        String [] args = {""};
        when(context.getArguments()).thenReturn(args);

        assertThrows(IllegalArgumentException.class, () -> subject.execute(context));
    }

    @Test
    void executedReturnResults()
    {
        subject = new SimpleLuceneSearchFunction(ls);
        when(context.getArguments()).thenReturn(validArgs);
        when(context.getResultSender()).thenReturn(rs);
        when(ls.createLuceneQueryFactory()).thenReturn(factory);
        when(factory.create(indexName,regionName,queryString,defaultField)).thenReturn(query);

        subject.execute(context);
        verify(rs).lastResult(any());
    }
}