#!/bin/bash
# Reference script for automating GemFire/JDK tar ball S3/http download

source ./setenv.sh
source ./common.library

# Make directory as need
mkdir -p $DOWNLOAD_DIR

#Download JDK
wget -P $DOWNLOAD_DIR $S3_ROOT/$JDK_TAR_BALL

#Download GemFire
wget -P $DOWNLOAD_DIR $S3_ROOT/$GEM_TAR_BALL

echo $S3_ROOT/$GEM_TAR_BALL

echo downloaded to $DOWNLOAD_DIR
ls $DOWNLOAD_DIR
