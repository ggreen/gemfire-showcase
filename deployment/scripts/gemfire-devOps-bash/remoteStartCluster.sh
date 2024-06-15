#!/bin/bash
# Starts cluster with all locators and data nodes
source ./setenv.sh
source ./common.library

## now loop through the above array
script_dir=$PWD

for i in $(cat ./config/locators)
do
   echo "Start locator on $i"

   ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH
   cd $script_dir
   source $PWD/setenv.sh
   source $PWD/common.library
    nohup $PWD/startLocator.sh >> /tmp/locator.log 2>&1 & 

  echo Waiting for Locator $i on port $LOCATOR_PORT

  while ! bash -c "echo > /dev/tcp/$HOSTNAME/$LOCATOR_PORT"; do sleep 10; done

echo  Locator $i started
EOSSH
done



sleep 30s
echo "Locators are started"
sleep 10s

for i in $(cat ./config/dataNodes)
do
   echo "Start data node on $i"

   ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH
   cd $script_dir
   source $PWD/setenv.sh
   source $PWD/common.library
   $PWD/startDataNode.sh 

EOSSH
done
