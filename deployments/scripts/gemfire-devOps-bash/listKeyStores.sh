#!/bin/bash
source ./setenv.sh

$JAVA_HOME/bin/keytool  -list -v -alias gemfire -storepass $CERT_PASSWORD -keystore $SECURITY_DIR/keystore.jks