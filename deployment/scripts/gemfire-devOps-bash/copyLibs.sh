#!/bin/bash
# Copies needed scripts (ex: startDataNode.sh) to cluster member servers

source ./setenv.sh
source ./common.library

## now loop through the above array
for i in $(cat ./config/locators ./config/dataNodes)
do

  echo $i

ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH
  mkdir -p $PWD/lib 

EOSSH

  scp $SSH_IDENTITY $PWD/lib/*.* $GEM_USER@$i:$PWD/lib/
done

