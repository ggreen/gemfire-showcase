package com.vmware.data.services.gemfire.lucene.functions.domain;


import java.io.Serializable;
import java.util.Objects;

import static java.lang.String.valueOf;

/**
 * Data transfer object for performing a gemfire search
 * @author gregory green
 */
public class SearchCriteria implements Serializable
{

	private static final String DEFAULT_PAGE_REGION_NM = "Paging";

	private int pageSize;
	private final String id;
	private final String query;
	private String pageRegionName = DEFAULT_PAGE_REGION_NM;
	private final String indexName;
	private final String defaultField;

	private final int limit;

	public boolean isKeysOnly() {
		return keysOnly;
	}

	public void setKeysOnly(boolean keysOnly) {
		this.keysOnly = keysOnly;
	}

	private boolean keysOnly;

	public SearchCriteria(String id, String indexName, String defaultField, String query, int limit) {
        this.id = id;
        this.query = query;
        this.defaultField = defaultField;
		this.indexName = indexName;
        this.limit = limit;
    }

	public static SearchCriteriaBuilder builder() {
		return new SearchCriteriaBuilder();
	}

	public String getId() {
		return id;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6542273931404555584L;
	/**
	 * @return the query
	 */
	public String getQuery()
	{
		return query;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName()
	{
		return indexName;
	}
	/**
	 * @return the defaultField
	 */
	public String getDefaultField()
	{
		return defaultField;
	}



	public String toPageKey(int pageNumber)
	{
		return String.join("-",getId() , valueOf(pageNumber));
	}
	

	/**
	 * @return the pageRegionName
	 */
	public String getPageRegionName()
	{
		return pageRegionName;
	}
	/**
	 * @param pageRegionName the pageRegionName to set
	 */
	public void setPageRegionName(String pageRegionName)
	{
		this.pageRegionName = pageRegionName;
	}

	/**
	 * @return the limit
	 */
	public int getLimit()
	{
		return limit;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TextPageCriteria{");
		sb.append("size=").append(pageSize);
		sb.append(", id='").append(id).append('\'');
		sb.append(", query='").append(query).append('\'');
		sb.append(", pageRegionName='").append(pageRegionName).append('\'');
		sb.append(", indexName='").append(indexName).append('\'');
		sb.append(", defaultField='").append(defaultField).append('\'');
		sb.append(", limit=").append(limit);
		sb.append(", keysOnly=").append(keysOnly);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SearchCriteria that = (SearchCriteria) o;
		return pageSize == that.pageSize && limit == that.limit && keysOnly == that.keysOnly && Objects.equals(id, that.id) && Objects.equals(query, that.query) && Objects.equals(pageRegionName, that.pageRegionName) && Objects.equals(indexName, that.indexName) && Objects.equals(defaultField, that.defaultField);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pageSize, id, query, pageRegionName, indexName, defaultField, limit, keysOnly);
	}

	public boolean getKeysOnly() {
		return keysOnly;
	}
}