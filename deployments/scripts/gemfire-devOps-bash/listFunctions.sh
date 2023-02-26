#!/bin/bash
# Runs the gfsh list members command

source ./setenv.sh
source ./common.library

$GEMFIRE_HOME/bin/gfsh -e ="connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" -e "list functions"
