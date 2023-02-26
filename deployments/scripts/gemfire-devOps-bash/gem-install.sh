#!/bin/bash
# Reference script for install of GemFire/JDK from downloaded tar balls
source ./setenv.sh
source ./common.library

mkdir -p $JAVA_INSTALL
mkdir -p $GEMFIRE_INSTALL_DIR

#Download JDK
tar xvf $DOWNLOAD_DIR/$JDK_TAR_BALL -C $JAVA_INSTALL

#Download GemFire
tar xvf $DOWNLOAD_DIR/$GEM_TAR_BALL -C  $GEMFIRE_INSTALL_DIR

echo Install JAVA_INSTALL=$JAVA_INSTALL GEMFIRE_INSTALL_DIR=$GEMFIRE_INSTALL_DIR
