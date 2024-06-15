#!/bin/bash
# Uses the gfsh compact disk-store command (schedule daily run off peak)

source ./setenv.sh
source ./common.library
#---------------------------------
# Print out for debugging
echo DISTRIBUTED_ID=$DISTRIBUTED_ID
echo REMOTE_DISTRIBUTED_ID=$REMOTE_DISTRIBUTED_ID


# Configure PDX cache server
$GEMFIRE_HOME/bin/gfsh -e ="connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "compact disk-store --name=DATA_DISKSTORE" -e "compact disk-store --name=PDX_DISK"  -e "compact disk-store --name=GATEWAY_DISK_STORE"
