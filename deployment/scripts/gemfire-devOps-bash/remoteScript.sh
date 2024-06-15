#!/bin/bash
# Wrapper for executing scripts remotely using ssh 

source ./setenv.sh 
source ./common.library

export REMOTE_SCRIPT_DIR=$PWD

 ssh $SSH_IDENTITY $GEM_USER@$1 <<+
    cd $REMOTE_SCRIPT_DIR;"$2" "$3" $4 $5
+
