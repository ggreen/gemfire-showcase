package com.vmware.data.services.gemfire.lucene.functions.domain;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

public class ClientSearchTest {
    private static int pageSize = 10;
    private static int limit = 1000;
    private static String indexName = "";
    private static String regionName = "";
    private static String queryString = "";
    private static String defaultField = "";

    public static void main(String[] args) throws LuceneQueryException {

        var clientCache = ClientCacheFactory.getAnyInstance();

        var luceneService = LuceneServiceProvider.get(clientCache);

        var factory = luceneService.createLuceneQueryFactory();
        factory.setPageSize(pageSize);
        factory.setLimit(limit);

        var query = factory.create(
                indexName,
                regionName,
                queryString,
                defaultField
        );

        var pages = query.findPages();
        while(pages.hasNext()) {
            for(var luceneResult : pages.next()) {
                //....
            }
        }

    }
}
