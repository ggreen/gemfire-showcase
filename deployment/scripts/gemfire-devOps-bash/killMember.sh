#!/bin/bash

# Kills all GemFire JVM processes running on the current server

PIDS=`ps -ef | grep gemfire | grep java| grep Launcher | awk '{print $2}'`


for memberPID in ${PIDS}
do

  echo Stopping process $memberPID
  kill -6 $memberPID

done
