#!/bin/bash
# Starts cluster with all locators and data nodes
source ./setenv.sh
source ./common.library

## now loop through the above array
script_dir=$PWD

for i in $(cat ./config/locators)
do
   echo "Restart locator on $i"

   ssh $SSH_IDENTITY $GEM_USER@$i  << EOSSH
   cd $script_dir
   $script_dir/restartLocator.sh
EOSSH

done


for i in  $(cat ./config/dataNodes)
do
   echo "Start data node on $i"

   ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH
   cd $script_dir
   $PWD/restartDataNode.sh
EOSSH
done
