#!/bin/bash

source ./setenv.sh
source ./common.library


## now loop through the above array
script_dir=$PWD
for i in $(cat ./config/locators ./config/dataNodes );
do

     echo "Cleaning on $i"
     ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH
     cd $script_dir
      $PWD/clean.sh
EOSSH

done
