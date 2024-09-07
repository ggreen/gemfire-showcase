package com.vmware.data.services.gemfire.lucene.functions.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaTest {
    @Test
    void builder() {
        assertNotNull(SearchCriteria.builder());
    }
}