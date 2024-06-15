#!/bin/bash
source ./setenv.sh
source ./common.library

if [ "$#" -ne 1 ]; then
    echo "Usage $0 password"
    exit;
fi


java io.pivotal.dataTx.geode.security.SecurityCryption $1
