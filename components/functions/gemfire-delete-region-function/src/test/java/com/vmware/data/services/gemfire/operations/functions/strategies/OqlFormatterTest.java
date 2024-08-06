package com.vmware.data.services.gemfire.operations.functions.strategies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OqlFormatterTest {

    private OqlFormatter subject = new OqlFormatter();

    @Test
    void comma() {
        String oql = "Select * from /region where set in (11{COMMA}11)";

        assertEquals("Select * from /region where set in (11,11)", subject.format(oql));
    }

    @Test
    void noComma() {
        String oql = "Select * from /region where set in (11)";

        assertEquals("Select * from /region where set in (11)", subject.format(oql));
    }
}