#!/bin/bash

source ./setenv.sh
source ./common.library


# Check Redundancies

./showRegionMetrics.sh test_copies | grep numBucketsWithoutRedundancy

REGIONS=`$GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATORS --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" -e "list regions" |  sed -n '/List of regions/,/ $/p' | sed -n '/---------------/,/ $/p' |  grep -v "\--"`
MEMBERS=`$GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATORS --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" -e "list members" |   sed -n '/Executing - list members/,/ $/p'| awk '{print $1}'| grep -E ".*$CS_NM.*"`


DATA_NODE_NM=$MEMBER_HOST_NM
DATA_NODE_NM+="_"
DATA_NODE_NM+=$CS_NM

for region in ${REGIONS}
do
    #DO NOT execute this operation in a parallel
    # export can be expensive operations
    numBucketsWithoutRedundancy=`$GEMFIRE_HOME/bin/gfsh -e "connect --locator=$LOCATORS --security-properties-file=$SECURITY_DIR/gfsecurity.properties  --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD" \
    -e "show metrics --region=/$region --member=$DATA_NODE_NM" | grep numBucketsWithoutRedundancy|  awk '{print $4}'`
    echo region $region  numBucketsWithoutRedundancy  "$numBucketsWithoutRedundancy"

    if [ $numBucketsWithoutRedundancy -gt 0 ]
    then
        echo "Cannot restart because region $region numBucketsWithoutRedundancy $numBucketsWithoutRedundancy > -1"
        exit -1
    fi
done




./stopDataNode.sh
./startDataNode.sh

echo Waiting for Data node $MEMBER_HOST_NM on port $CS_PORT

while ! bash -c "echo > /dev/tcp/$HOSTNAME/$CS_PORT"; do sleep 10; done

echo  Data node $MEMBER_HOST_NM restarted
