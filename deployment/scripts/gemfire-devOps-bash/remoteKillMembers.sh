#!/bin/bash
# Executes killMember.sh on locators and datanodes

source ./setenv.sh
source ./common.library

## now loop through the above array
script_dir=$PWD
for i in $(cat ./config/locators);
do

ssh $SSH_IDENTITY -T $GEM_USER@$i << EOSSH

   echo "Killing locator on $i"
   cd $script_dir
   ./killMember.sh $1
EOSSH

done


for i in $(cat ./config/dataNodes );
do

ssh $SSH_IDENTITY -T $GEM_USER@$i << EOSSH

   echo "Killing datanode on $i"
   cd $script_dir
   ./killMember.sh $1
EOSSH

done
