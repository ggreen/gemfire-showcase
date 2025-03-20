package com.vmware.pivotal.labs.services.dataTx.geode.office.logs;

import com.vmware.pivotal.labs.services.dataTx.geode.office.logs.domain.LogError;

import java.io.File;

public class LogAnalyzer {
    private final File directory;

    public LogAnalyzer(File directory) {
        this.directory = directory;
    }

    public LogError firstError() {
        return null;
    }
}
