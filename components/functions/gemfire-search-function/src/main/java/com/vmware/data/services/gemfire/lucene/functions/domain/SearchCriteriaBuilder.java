package com.vmware.data.services.gemfire.lucene.functions.domain;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxSerializationException;

/**
 * Builder for TextPageCriteria based on input arguments such as string arrays (ex: Gfsh arguments) or Pdx
 * @author gregory green
 */
public class SearchCriteriaBuilder {
    private static final int DEFAULT_LIMIT = 1000;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private Object args;

    private String id;
    private String query;
    private String regionName;
    private String defaultField;
    private int limit;
    private String indexName;

    /**
     * Set the input arguments
     * @param args the input arguments contain search details
     * @return the builder instances
     */
    public SearchCriteriaBuilder args(Object args){
        this.args = args;
        return this;
    }

    public SearchCriteria build() {

        if(args instanceof PdxInstance)
        {
            PdxInstance  pdxInstance = (PdxInstance)args;

            try
            {
                return (SearchCriteria)(pdxInstance.getObject());
            }
            catch (PdxSerializationException e)
            {
                throw new FunctionException(e.getMessage()+" JSON:"+ JSONFormatter.toJSON(pdxInstance));
            }
        }
        if(args instanceof String[])
        {
            String[] argsStrings = (String[])args;
            int argIndex = 0;
            if(argsStrings.length > argIndex)
                id = argsStrings[argIndex++];


            if(argsStrings.length > argIndex)
                regionName = argsStrings[argIndex++];


            if(argsStrings.length > argIndex)
                indexName = argsStrings[argIndex++];

            if(argsStrings.length > argIndex)
                defaultField = argsStrings[argIndex++];

            if(argsStrings.length > argIndex)
                query = argsStrings[argIndex++];

            if(argsStrings.length > argIndex)
                limit = toInteger(argsStrings[argIndex++],DEFAULT_LIMIT,"limit");

            SearchCriteria searchCriteria = new SearchCriteria(id, regionName,indexName, defaultField, query, limit);

            if(argsStrings.length > argIndex)
                searchCriteria.setPageSize(toInteger(argsStrings[argIndex++],DEFAULT_PAGE_SIZE,"pageSize"));


            if(argsStrings.length > argIndex)
                searchCriteria.setKeysOnly(toBoolean(argsStrings[argIndex++],false,"keysOnly"));

            return searchCriteria;
        }
        else
        {
            return (SearchCriteria)args;
        }
    }

    private boolean toBoolean(String text, boolean defaultValue, String fieldName) {
        try {
            if(text == null || text.isEmpty())
                return defaultValue;

            return Boolean.parseBoolean(text);

        }catch(NumberFormatException e)
        {
            throw new NumberFormatException("Unable to parse  \""+fieldName+"\" expected boolean, but received:"+ text);
        }
    }

    /**
     * Convert text to integer
     * @param text the test to parse
     * @param defaultValue the default if text is empty
     * @param fieldName the field for error handling
     * @return the integer value
     */
    private int toInteger(String text, int defaultValue, String fieldName) {
        try {
            if(text == null || text.isEmpty())
                return defaultValue;

            return Integer.parseInt(text);

        }catch(NumberFormatException e)
        {
            throw new NumberFormatException("Unable to parse  \""+fieldName+"\" expected number, but received:"+ text);
        }
    }

    public SearchCriteriaBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SearchCriteriaBuilder query(String query) {
        this.query = query;
        return this;
    }

    public SearchCriteriaBuilder regionName(String region) {
        this.regionName = region;
        return this;
    }

    public SearchCriteriaBuilder indexName(String index) {
        this.indexName = index;
        return this;
    }
    public SearchCriteriaBuilder defaultField(String index) {
        this.defaultField = defaultField;
        return this;
    }
    public SearchCriteriaBuilder limit(int index) {
        this.limit = limit;
        return this;
    }
}
