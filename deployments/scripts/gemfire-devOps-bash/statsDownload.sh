#!/bin/bash
# Starts cluster with all locators and data nodes


source ./setenv.sh
echo Copying
source ./common.library

## now loop through the above array

for i in $(cat ./config/locators)
do
   echo "Coping locatori stats on $i"


   echo Copying from Locator $i
   mkdir -p $DOWNLOAD_DIR/stats/locator/$i/
   scp -r $SSH_IDENTITY $GEM_USER@$i:$WORK_DIR/*/*.gfs $DOWNLOAD_DIR/stats/locator/$i/
   echo copied to $DOWNLOAD_DIR/stats/locator/$i/
done


for i in $(cat ./config/dataNodes)
do
   echo "Copying Data node on $i"


   echo Copying from Data Node $i
   mkdir -p $DOWNLOAD_DIR/stats/dataNode/$i/
   scp -r $SSH_IDENTITY $GEM_USER@$i:$WORK_DIR/*/*.gfs $DOWNLOAD_DIR/stats/dataNode/$i/
   echo copied to $DOWNLOAD_DIR/stats/dataNode/$i/
done
