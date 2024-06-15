#!/bin/bash
# Reference script for install of GemFire/JDK from downloaded tar balls

source ./setenv.sh
source ./common.library

for i in $(cat ./config/dataNodes ./config/locators);
do
    echo $i


# Setup directories
ssh $SSH_IDENTITY -T $GEM_USER@$i << EOSSH

    mkdir -p $ROOT_DIR
    chown -R $GEM_USER $ROOT_DIR
    mkdir -p $DOWNLOAD_DIR
    mkdir -p $JAVA_INSTALL
    mkdir -p $GEMFIRE_INSTALL_DIR

    echo DOWNLOAD_DIR=$DOWNLOAD_DIR on user=$USER host=$i
    ls $DOWNLOAD_DIR/..
EOSSH

  #Copy Files 
  ssh $SSH_IDENTITY $GEM_USER@$i  mkdir -p $PWD/config/
  scp $SSH_IDENTITY -r  ./config/* $GEM_USER@$i:$PWD/config/
  scp $SSH_IDENTITY $DOWNLOAD_DIR/$JDK_TAR_BALL $GEM_USER@$i:$DOWNLOAD_DIR/$JDK_TAR_BALL
  scp $SSH_IDENTITY $DOWNLOAD_DIR/$GEM_TAR_BALL $GEM_USER@$i:$DOWNLOAD_DIR/$GEM_TAR_BALL

  #show output

ssh $SSH_IDENTITY -T $GEM_USER@$i << EOSSH

  ls $DOWNLOAD_DIR

  #Install JDK
  ls $DOWNLOAD_DIR
  tar xvf $DOWNLOAD_DIR/$JDK_TAR_BALL -C $JAVA_INSTALL

  #Install GemFire
  tar xvf $DOWNLOAD_DIR/$GEM_TAR_BALL -C  $GEMFIRE_INSTALL_DIR

EOSSH

   #mkdir -p $JAVA_INSTALL
   #mkdir -p $GEMFIRE_INSTALL_DIR

   #Download JDK
   #tar xvf $DOWNLOAD_DIR/$JDK_TAR_BALL -C $JAVA_INSTALL

   #Download GemFire
   #tar xvf $DOWNLOAD_DIR/$GEM_TAR_BALL -C  $GEMFIRE_INSTALL_DIR

   #echo Install JAVA_INSTALL=$JAVA_INSTALL GEMFIRE_INSTALL_DIR=$GEMFIRE_INSTALL_DIR

done


./remoteSyncScripts.sh
./copyLibs.sh


