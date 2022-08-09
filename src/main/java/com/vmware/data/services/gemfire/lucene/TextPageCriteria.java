package com.vmware.data.services.gemfire.lucene;


import java.util.Set;

import nyla.solutions.core.patterns.iteration.PageCriteria;

public class TextPageCriteria extends PageCriteria
{


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
	 * @param query the query to set
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}
	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}
	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName)
	{
		this.indexName = indexName;
	}
	/**
	 * @param defaultField the defaultField to set
	 */
	public void setDefaultField(String defaultField)
	{
		this.defaultField = defaultField;
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
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextPageCriteria other = (TextPageCriteria) obj;
		if (defaultField == null)
		{
			if (other.defaultField != null)
				return false;
		}
		else if (!defaultField.equals(other.defaultField))
			return false;
		if (filter == null)
		{
			if (other.filter != null)
				return false;
		}
		else if (!filter.equals(other.filter))
			return false;
		if (indexName == null)
		{
			if (other.indexName != null)
				return false;
		}
		else if (!indexName.equals(other.indexName))
			return false;
		if (limit != other.limit)
			return false;
		if (pageRegionName == null)
		{
			if (other.pageRegionName != null)
				return false;
		}
		else if (!pageRegionName.equals(other.pageRegionName))
			return false;
		if (query == null)
		{
			if (other.query != null)
				return false;
		}
		else if (!query.equals(other.query))
			return false;
		if (regionName == null)
		{
			if (other.regionName != null)
				return false;
		}
		else if (!regionName.equals(other.regionName))
			return false;
		if (sortDescending != other.sortDescending)
			return false;
		if (sortField == null)
		{
			if (other.sortField != null)
				return false;
		}
		else if (!sortField.equals(other.sortField))
			return false;
		return true;
	}



	private String query;
	private String regionName;
	private String pageRegionName;
	private String indexName;
	private String defaultField;
	private String sortField;
	private boolean sortDescending = false;
	
	//private String id;
	//private int pageSize;
	private Set<?> filter;
	private int limit;
	//private int pageNumber ;
}