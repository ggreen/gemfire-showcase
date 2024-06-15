#!/bin/bash
#  Clean files and directories in the member's working directory
source ./setenv.sh
source ./common.library

read -r -p "Note: This will remove all the local data and or configuration. Are you sure? [Y/n]:" response
if [[ $response =~ ^[Nn] ]]
then
  exit;
fi

echo cleaning logs $WORK_DIR/$CS_NM/
echo cleaning logs $WORK_DIR/$LOCATOR_NM

rm  $WORK_DIR/$CS_NM/*.log
rm $WORK_DIR/$LOCATOR_NM/*.log
