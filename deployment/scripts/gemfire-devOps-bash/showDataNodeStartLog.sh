#!/bin/bash
# Starts a cache server data node on the local server
source ./setenv.sh
source ./common.library

cat $WORK_DIR/$CS_NM/"$MEMBER_HOST_NM"_$CS_NM.log
echo LOG LOCATION: $WORK_DIR/$CS_NM/"$MEMBER_HOST_NM"_$CS_NM.log
