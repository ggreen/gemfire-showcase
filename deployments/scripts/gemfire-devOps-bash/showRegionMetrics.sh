#!/bin/bash
# Starts a cache server data node on the local server
source ./setenv.sh
source ./common.library


if [ "$#" -ne 1 ]; then
    echo "Usage $0 region"
    exit;
fi


if [ ! -f "$SECURITY_DIR/gfsecurity.properties" ]
then

   echo ERROR: $SECURITY_DIR/gfsecurity.properties does not exist. See selfSignCert.sh or create it manually  1>&2

   exit 1
fi

if [ -z $SECURITY_USERNAME ]
then
  echo "Missing setenv property SECURITY_USERNAME"
  exit 1
fi



#---------------------------------
$GEMFIRE_HOME/bin/gfsh -e ="connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" -e "show metrics --region=$1"
