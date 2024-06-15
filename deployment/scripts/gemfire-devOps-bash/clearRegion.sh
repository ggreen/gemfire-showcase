#!/bin/bash
# Runs the gfsh list members command


if [ "$#" -ne 1 ]; then
    echo "Usage $0 region"
    exit;
fi


source ./setenv.sh
source ./common.library


  echo clearing region $region
  $GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" -e "execute function --id=ClearRegionFunction --region=$1"
