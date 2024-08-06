package com.vmware.data.services.gemfire.operations.functions.strategies;

public class OqlFormatter {

    private static final String COMMA_ENCODE = "{COMMA}";

    public String format(String oql) {
        return oql.replace(COMMA_ENCODE,",");
    }
}
