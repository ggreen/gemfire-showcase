#!/bin/bash
# Starts cluster with all locators and data nodes
source ./setenv.sh
source ./common.library

## now loop through the above array

for i in $(cat ./config/locators)
do
   echo "Start locator on $i"


   echo Cleaning from Locator $i
    ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH

    rm $WORK_DIR/*/*.gfs
EOSSH

done


for i in $(cat ./config/dataNodes)
do
   echo "Start Data node on $i"

   echo Cleaning from Data Node $i
   ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH

   rm $WORK_DIR/*/*.gfs
EOSSH
done
