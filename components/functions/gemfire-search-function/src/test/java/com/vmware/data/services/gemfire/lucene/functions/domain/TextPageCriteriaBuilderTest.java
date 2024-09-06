package com.vmware.data.services.gemfire.lucene.functions.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.*;

class TextPageCriteriaBuilderTest {


    private TextPageCriteriaBuilder subject;

    @BeforeEach
    void setUp() {
        subject = new TextPageCriteriaBuilder();
    }

    @Test
    void fromArgs() {

        String id = "id"; String query = "query";
        String regionName = "region"; String indexName = "index";
        String defaultField = "field";
        int limit = 30;
        TextPageCriteria expected = new TextPageCriteria(id,query,regionName,indexName,defaultField,limit);

        String[] stringArgs = {
                expected.getId(),
                expected.getQuery(),
                expected.getRegionName(),
                expected.getIndexName(),
                expected.getDefaultField(),
                valueOf(expected.getLimit())};

        var actual = subject.args(stringArgs).build();

        assertEquals(expected, actual);
    }
}