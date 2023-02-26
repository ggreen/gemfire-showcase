#!/bin/bash
# Uses the GemFire gfsh shutdown to stop members gracefully

source ./setenv.sh
source ./common.library

$GEMFIRE_HOME/bin/gfsh -e ="connect --locator=$LOCATOR_GFSH_CONNECT --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD --security-properties-file=$SECURITY_DIR/gfsecurity.properties" -e "shutdown --include-locators=true"
