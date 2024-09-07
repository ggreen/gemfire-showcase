package com.vmware.data.services.gemfire.lucene.functions.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaBuilderTest {


    private static final String id = "id";
    private static final String query = "query";
    private static final String indexName = "index";
    private static final  String defaultField = "field";
    private static final int limit = 30;
    private static final int pageNumber = 0;

    private SearchCriteriaBuilder subject;
    private SearchCriteria expected;

    @BeforeEach
    void setUp() {
        this.expected =  new SearchCriteria(id,  indexName, defaultField,query,limit);

        subject = new SearchCriteriaBuilder();
    }

    @Test
    void fromArgs() {

        String[] stringArgs = {
                expected.getId(),
                expected.getIndexName(),
                expected.getDefaultField(),
                expected.getQuery(),
                valueOf(expected.getLimit())};

        var actual = subject.args(stringArgs).build();

        assertEquals(expected, actual);
    }

    @Test
    void fromArgs_pagingSize() {

        expected.setPageSize(23);

        String[] stringArgs = {
                expected.getId(),
                expected.getIndexName(),
                expected.getDefaultField(),
                expected.getQuery(),
                valueOf(expected.getLimit()),
                valueOf(expected.getPageSize())
        };

        var actual = subject.args(stringArgs).build();

        assertEquals(expected, actual);
    }

    @Test
    void fromArgs_keyOnlyTrue() {

        expected.setKeysOnly(true);

        String[] stringArgs = {
                expected.getId(),
                expected.getIndexName(),
                expected.getDefaultField(),
                expected.getQuery(),
                valueOf(expected.getLimit()),
                valueOf(expected.getPageSize()),
                valueOf(expected.isKeysOnly())
        };

        var actual = subject.args(stringArgs).build();

        assertEquals(expected, actual);
    }


    @Test
    void toPageKey() {
        assertEquals(id+"-"+pageNumber, expected.toPageKey(pageNumber));
    }
}