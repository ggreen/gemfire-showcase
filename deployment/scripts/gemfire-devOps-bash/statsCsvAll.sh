#!/bin/bash
source ./setenv.sh
source ./common.library

if [ ! -e $DOWNLOAD_DIR/stats/dataNode ]
then
  echo "File not downloaded to $DOWNLOAD_DIR/stats/dataNode. Please execute statsDownload.sh"
  exit 1
fi

echo Processing directory $DOWNLOAD_DIR/stats/dataNode

echo TODO
#java gedi.solutions.geode.operations.apps.StatsToCsvApp $DOWNLOAD_DIR/stats/dataNode


ls $DOWNLOAD_DIR/stats/dataNode
