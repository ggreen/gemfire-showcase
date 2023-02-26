#!/bin/bash
# Runs the gfsh list members command

source ./setenv.sh
source ./common.library

listRegion

for region in $REGIONS;
do

  echo clearing region $region
  $GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" -e "execute function --id=ClearRegionFunction --region=$region"

done
