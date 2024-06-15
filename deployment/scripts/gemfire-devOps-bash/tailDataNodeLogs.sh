#!/bin/bash
# Script that starts the gfsh shell

source ./setenv.sh
source ./common.library

tail -f $WORK_DIR/server/*.log
