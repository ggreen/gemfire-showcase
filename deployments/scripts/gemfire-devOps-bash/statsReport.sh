#!/bin/bash
source ./setenv.sh
source ./common.library

if [ -z $REPORT_DIR ]
then
  echo "Missing setenv property REPORT_DIR"
  exit 1
fi

mkdir -p $REPORT_DIR

echo $CLASSPATH
dateValue=`date +"%m/%d/%Y"`
echo TODO
#java -DDAY_FILTER=$dateValue gedi.solutions.geode.office.StatsToChartApp $DOWNLOAD_DIR/stats/dataNode  $REPORT_DIR
