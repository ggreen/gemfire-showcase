package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import java.io.File;

/**
   * Wraps an instance of StatSpec but alwasy returns a combine type of NONE.
   */
  public class RawStatSpec implements StatSpec {
    private final StatSpec spec;

    public RawStatSpec(StatSpec wrappedSpec) {
      this.spec = wrappedSpec;
    }

    public int getCombineType() {
      return StatSpec.NONE;
    }

    public boolean typeMatches(String typeName) {
      return spec.typeMatches(typeName);
    }

    public boolean statMatches(String statName) {
      return spec.statMatches(statName);
    }

    public boolean instanceMatches(String textId, long numericId) {
      return spec.instanceMatches(textId, numericId);
    }

    public boolean archiveMatches(File archive) {
      return spec.archiveMatches(archive);
    }
  }