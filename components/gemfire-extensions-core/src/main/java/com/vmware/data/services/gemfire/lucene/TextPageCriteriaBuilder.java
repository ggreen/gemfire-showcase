package com.vmware.data.services.gemfire.lucene;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxSerializationException;

public class TextPageCriteriaBuilder {
    private static final int DEFAULT_LIMIT = 1000;

    private Object args;

    private String id;
    private String query;
    private String regionName;
    private String defaultField;
    private int beginIndex;
    private int limit = DEFAULT_LIMIT;
    private String indexName;

    public TextPageCriteriaBuilder args(Object args){
        this.args = args;
        return this;
    }

    public TextPageCriteria build() {

        if(args instanceof PdxInstance)
        {
            PdxInstance  pdxInstance = (PdxInstance)args;

            try
            {
                return (TextPageCriteria)(pdxInstance.getObject());
            }
            catch (PdxSerializationException e)
            {
                throw new FunctionException(e.getMessage()+" JSON:"+ JSONFormatter.toJSON(pdxInstance));
            }
        }
        if(args instanceof String[])
        {
            String[] argsStrings = (String[])args;

            id = argsStrings[0];
            query = argsStrings[1];
            regionName = argsStrings[2];
            indexName = argsStrings[3];
            defaultField = argsStrings[4];
            String limitText = argsStrings[5];

            try {
                if(limitText != null && !limitText.isEmpty())
                    limit = Integer.parseInt(limitText);


            }catch(NumberFormatException e)
            {
                throw new NumberFormatException("Unable to parse limit:"+limitText);
            }

            return new TextPageCriteria(id, query, regionName, indexName,defaultField, limit);

        }
        else
        {
            return (TextPageCriteria)args;
        }

    }

    public TextPageCriteriaBuilder id(String id) {
        this.id = id;
        return this;
    }

    public TextPageCriteriaBuilder query(String query) {
        this.query = query;
        return this;
    }

    public TextPageCriteriaBuilder regionName(String region) {
        this.regionName = region;
        return this;
    }

    public TextPageCriteriaBuilder indexName(String index) {
        this.indexName = index;
        return this;
    }
    public TextPageCriteriaBuilder defaultField(String index) {
        this.defaultField = defaultField;
        return this;
    }
    public TextPageCriteriaBuilder limit(int index) {
        this.limit = limit;
        return this;
    }
}
