package com.vmware.data.services.apache.geode.lucene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

import nyla.solutions.core.data.MapEntry;
import nyla.solutions.core.util.BeanComparator;
import nyla.solutions.core.util.Debugger;


/**
 * Implement for searching Lucene
 *
 * @author Gregory Green
 */


public class TextPolicySearchStrategy
{
    /**
     * @param gemFireCache the cache
     */
    public TextPolicySearchStrategy(GemFireCache gemFireCache)
    {
        this(LuceneServiceProvider.get(gemFireCache));
    }//------------------------------------------------

    /**
     * @param luceneService the luceneService
     */
    public TextPolicySearchStrategy(LuceneService luceneService)
    {
        this.luceneService = luceneService;
    }//------------------------------------------------

    public <K, V> Collection<String> saveSearchResultsWithPageKeys(TextPageCriteria criteria,
                                                                   LuceneQueryProvider queryProvider,
                                                                   Predicate<LuceneResultStruct<K, V>> filter,
                                                                   Region<String, Collection<K>> pageKeysRegion)
    {
        return saveSearchResultsWithPageKeys(criteria, queryProvider.toString(), filter, pageKeysRegion);
    }//------------------------------------------------

    public <K, V> Collection<String> saveSearchResultsWithPageKeys(TextPageCriteria criteria, String query,
                                                                   Predicate<LuceneResultStruct<K, V>> filter,
                                                                   Region<String, Collection<K>> pageKeysRegion)
    {
        if (criteria == null)
            return null;

        if (criteria.getId() == null || criteria.getId().length() == 0)
            throw new IllegalArgumentException("Default criteria id is required");

        try {
            //clearing asynchronously
            GeodePagination pagination = new GeodePagination();

            pagination.clearSearchResultsByPage(criteria, pageKeysRegion);

            List<Map.Entry<K, V>> results = executeQuery(criteria, query, filter);

            return  pagination.storePaginationMap(criteria.getId(),
                    criteria.getEndIndex() - criteria.getBeginIndex(), pageKeysRegion,
                    results);

        }
        catch (LuceneQueryException e) {
            throw new FunctionException(e);
        }
    }//------------------------------------------------

    /**
     * @param criteria      the text page criteria
     * @param queryProvider the query provider
     * @param filter        filter to the records
     * @return collection of entries
     * @throws LuceneQueryException when Apac Lucene occurs
     */
    <K, V> List<Map.Entry<K, V>> executeQuery(TextPageCriteria criteria,
                                              LuceneQueryProvider queryProvider,
                                              Predicate<LuceneResultStruct<K, V>> filter) throws LuceneQueryException
    {

        String query = queryProvider.getQuery(null).toString();

        return executeQuery(criteria, query, filter);
    }

    public <K, V> List<Map.Entry<K, V>> executeQuery(TextPageCriteria criteria, String query,
                                                     Predicate<LuceneResultStruct<K, V>> filter)
            throws LuceneQueryException
    {
        if (query == null || query.length() == 0)
            throw new FunctionException("Query provider results text is empty");


        LuceneQuery<K, V> luceneQuery;


        luceneQuery = luceneService.createLuceneQueryFactory()
                                   .create(criteria.getIndexName(), criteria.getRegionName(), query,
                                           criteria.getDefaultField());


        Debugger.println("criteria:" + criteria);

        List<LuceneResultStruct<K, V>> list = luceneQuery.findResults();

        if (list == null || list.isEmpty()) {
            Debugger.println(new StringBuilder().append(criteria.getId()).append(" lucene results cnt:0"));
            return null;
        }

        Debugger.println(new StringBuilder().append(criteria.getId()).append(" lucene results cnt:").append(list.size()));


        if (filter != null) {
            //filter records
            list = list.parallelStream().filter(filter).collect(Collectors.toList());
        }

        if (list.isEmpty())
            return null;

        Debugger.println(new StringBuilder().append(criteria.getId()).append(" FILTERED lucene results cnt:").append(list.size()));


        String sortField = criteria.getSortField();
        BeanComparator beanComparator;

        List<Map.Entry<K, V>> results;
        if (sortField != null && sortField.length() > 0) {

            if (!sortField.startsWith("value."))
                sortField = "value.".concat(sortField);

            //sorted results
            beanComparator = new BeanComparator(sortField, criteria.isSortDescending());

            Collection<Map.Entry<K, V>> set = new TreeSet<>(beanComparator);

            list.parallelStream().forEach(e -> set.add(new MapEntry<>(e.getKey(), e.getValue())));

            //convert to array
            results = new ArrayList<>(set);

        } else {
            //Un-order results
            results = list.stream().map(e -> new MapEntry<>(e.getKey(), e.getValue())).collect(Collectors.toList());
        }

        return results;
    }


    private final LuceneService luceneService;

}