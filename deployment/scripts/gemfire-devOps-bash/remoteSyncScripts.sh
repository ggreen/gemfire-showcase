#!/bin/bash
# Copies needed scripts (ex: startDataNode.sh) to cluster member servers

source ./setenv.sh
source ./common.library

 

## now loop through the above array
for i in $(cat ./config/locators ./config/dataNodes)
do

  echo $i
  if [[ ! $i =~ $HOSTNAME.* ]]
  then

ssh $SSH_IDENTITY $GEM_USER@$i << EOSSH

    echo $i
    mkdir -p $PWD/lib
    mkdir -p "$SECURITY_DIR"
    chown $GEM_USER $SECURITY_DIR
    mkdir -p  $PWD/schemas
    mkdir -p  $PWD/config
    chown $GEM_USER $PWD/schemas

EOSSH

   echo "Copying onto locator  $GEM_USER@$i:$PWD"
   scp $SSH_IDENTITY *.* $GEM_USER@$i:$PWD
   scp $SSH_IDENTITY $PWD/lib/* $GEM_USER@$i:$PWD/lib/
   scp $SSH_IDENTITY $PWD/config/* $GEM_USER@$i:$PWD/config/
   scp $SSH_IDENTITY $PWD/schemas/*.* *.sh $GEM_USER@$i:$PWD/schemas/

    echo "COPYING certicate directory $SECURITY_DIR"
    scp $SSH_IDENTITY $SECURITY_DIR/*  $GEM_USER@$i:$SECURITY_DIR/

  fi

done
