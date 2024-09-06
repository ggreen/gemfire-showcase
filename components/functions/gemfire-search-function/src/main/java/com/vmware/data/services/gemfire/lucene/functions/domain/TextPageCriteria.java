package com.vmware.data.services.gemfire.lucene.functions.domain;


import java.io.Serializable;
import java.util.Objects;
import java.util.Set;


public class TextPageCriteria implements Serializable
{

	private static final String DEFAULT_PAGE_REGION_NM = "Paging";

	private int size;
	private final String id;
	private final String query;
	private final String regionName;
	private String pageRegionName = DEFAULT_PAGE_REGION_NM;
	private final String indexName;
	private final String defaultField;
	private int beginIndex;
	private String sortField;
	private boolean sortDescending = false;

	private Set<?> filter;
	private final int limit;

	public boolean isKeysOnly() {
		return keysOnly;
	}

	public void setKeysOnly(boolean keysOnly) {
		this.keysOnly = keysOnly;
	}

	private boolean keysOnly;

	public TextPageCriteria(String id, String query, String regionName, String indexName, String defaultField, int limit) {
        this.id = id;
        this.query = query;
        this.regionName = regionName;
        this.defaultField = defaultField;
		this.beginIndex = 0;
		this.indexName = indexName;
        this.limit = limit;
    }

	public static TextPageCriteriaBuilder builder() {
		return new TextPageCriteriaBuilder();
	}

	public String getId() {
		return id;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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
	 * @return the regionName
	 */
	public String getRegionName()
	{
		return regionName;
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

	
	/**
	 * @return the sortField
	 */
	public String getSortField()
	{
		return sortField;
	}
	/**
	 * @param sortField the sortField to set
	 */
	public void setSortField(String sortField)
	{
		this.sortField = sortField;
	}

	public String toPageKey(int pageNumber)
	{
		return new StringBuilder().append(this.getId()).append("-").append(pageNumber).toString();
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
	 * @return the filter
	 */
	public Set<?> getFilter()
	{
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(Set<?> filter)
	{
		this.filter = filter;
	}

	/**
	 * @return the sortDescending
	 */
	public boolean isSortDescending()
	{
		return sortDescending;
	}
	/**
	 * @param sortDescending the sortDescending to set
	 */
	public void setSortDescending(boolean sortDescending)
	{
		this.sortDescending = sortDescending;
	}


	/**
	 * @return the limit
	 */
	public int getLimit()
	{
		return limit;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((defaultField == null) ? 0 : defaultField.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((indexName == null) ? 0 : indexName.hashCode());
		result = prime * result + limit;
		result = prime * result + ((pageRegionName == null) ? 0 : pageRegionName.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((regionName == null) ? 0 : regionName.hashCode());
		result = prime * result + (sortDescending ? 1231 : 1237);
		result = prime * result + ((sortField == null) ? 0 : sortField.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TextPageCriteria that = (TextPageCriteria) o;
		return size == that.size && beginIndex == that.beginIndex && sortDescending == that.sortDescending && limit == that.limit && Objects.equals(id, that.id) && Objects.equals(query, that.query) && Objects.equals(regionName, that.regionName) && Objects.equals(pageRegionName, that.pageRegionName) && Objects.equals(indexName, that.indexName) && Objects.equals(defaultField, that.defaultField) && Objects.equals(sortField, that.sortField) && Objects.equals(filter, that.filter);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TextPageCriteria{");
		sb.append("size=").append(size);
		sb.append(", id='").append(id).append('\'');
		sb.append(", query='").append(query).append('\'');
		sb.append(", regionName='").append(regionName).append('\'');
		sb.append(", pageRegionName='").append(pageRegionName).append('\'');
		sb.append(", indexName='").append(indexName).append('\'');
		sb.append(", defaultField='").append(defaultField).append('\'');
		sb.append(", beginIndex=").append(beginIndex);
		sb.append(", sortField='").append(sortField).append('\'');
		sb.append(", sortDescending=").append(sortDescending);
		sb.append(", filter=").append(filter);
		sb.append(", limit=").append(limit);
		sb.append('}');
		return sb.toString();
	}

	public int getEndIndex()
	{
		return this.beginIndex + size;
	}

	public boolean getKeysOnly() {
		return keysOnly;
	}
}