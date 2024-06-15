#!/bin/bash
# Script that imports all region data exported by exportAllData.sh

source ./setenv.sh
source ./common.library

REGIONS=`$GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "list regions" |  sed -n '/List of regions/,/ $/p' | sed -n '/---------------/,/ $/p' |  grep -v "\--"`
MEMBERS=`$GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "list members" |   sed -n '/Executing - list members/,/ $/p'| awk '{print $1}'| grep -E ".*$CS_NM.*"`


for member in ${MEMBERS}
do
  for region in ${REGIONS}
  do
    #DO NOT execute this operation in a parallel
    # import can be expensive operations
    $GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD --security-properties-file=$SECURITY_DIR/gfsecurity.properties" -e "import data --region=$region --dir=$BACKUP_DIR --member=$member --parallel=true"
  done

done
