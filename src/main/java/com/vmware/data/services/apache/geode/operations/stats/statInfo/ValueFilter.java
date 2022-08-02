package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.File;

/**
   * Specifies what data from a statistic archive will be of interest to the
   * reader. This is used when loading a statistic archive file to reduce the
   * memory footprint. Only statistic data that matches all four will be
   * selected for loading.
   */
  public interface ValueFilter {

	public boolean archiveMatches(File archive);

    public boolean typeMatches(String typeName);

    public boolean statMatches(String statName);


    public boolean instanceMatches(String textId, long numericId);
  }