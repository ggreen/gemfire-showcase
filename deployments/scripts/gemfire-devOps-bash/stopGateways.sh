#!/bin/bash
#  Exports all gateway data for each member in parallel
source ./setenv.sh
source ./common.library

echo "WARNING this script is experimental"

GATEWAYSS=`$GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "list gateways" |  sed -n '/GatewaySender Id/,/ $/p' | sed -n '/---------------/,/ $/p'| grep "_sender" |  grep -v "\--" | awk '{print $1}'`


    echo stopping gateway-receiver

    $GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "stop gateway-receiver"

  for gatewaysender in ${GATEWAYSS}
  do

    echo stopping $gatewaysender
    $GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATOR_GFSH_CONNECT --security-properties-file=$SECURITY_DIR/gfsecurity.properties --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD " -e "stop gateway-sender --id=$gatewaysender"
  done
