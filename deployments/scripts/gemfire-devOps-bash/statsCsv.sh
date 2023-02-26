#!/bin/bash
source ./setenv.sh
source ./common.library

if [ "$#" -ne 1 ]
then
  echo "Usage " $0 "fileOrDirectory"
  exit 1
fi

echo Processing $1

echo TODO
#java -mx1g -mx1g gedi.solutions.geode.operations.apps.StatsToCsvApp $1
