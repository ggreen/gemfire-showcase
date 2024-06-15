#!/bin/bash
# Creates backup for disk stores using the gfsh backup disk-store command

source ./setenv.sh
source ./common.library

./compactDiskStores.sh $@


$GEMFIRE_HOME/bin/gfsh -e ="connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "backup disk-store --dir=$BACKUP_DIR"
