#!/bin/bash
# Script generates a self signed security keys using JDK keytool

source ./setenv.sh
source ./common.library

#------------


if [ -z $SSL_ENABLED_COMPONENTS ]
then
  echo "Missing setenv property SSL_ENABLED_COMPONENTS"
  exit 1
fi


if [ -z $CERT_PASSWORD ]
then
  echo "Missing setenv property CERT_PASSWORD"
  exit 1
fi

if [ -z $SECURITY_DIR ]
then
  echo "Missing setenv property SECURITY_DIR"
  exit 1
fi

if [ -z $CERT_VALIDITY_DAYS ]
then
  echo "Missing setenv property CERT_VALIDITY_DAYS"
  exit 1
fi

export MEMBER_FULL_HOST_NM=`hostname -s`

mkdir -p $SECURITY_DIR

# -validity 3650 \
# -keypass password \

$JAVA_HOME/bin/keytool  -delete -noprompt  -alias gemfire  -keystore $SECURITY_DIR/keystore.jks -storepass $CERT_PASSWORD


$JAVA_HOME/bin/keytool  -delete -noprompt  -alias gemfire  -keystore $SECURITY_DIR/truststore.jks -storepass $CERT_PASSWORD



$JAVA_HOME/bin/keytool -genkey -alias gemfire -keyalg RSA -keypass $CERT_PASSWORD -storepass $CERT_PASSWORD -validity $CERT_VALIDITY_DAYS -alias gemfire -dname "CN=${MEMBER_FULL_HOST_NM}" -keysize 2048  -keystore $SECURITY_DIR/keystore.jks



$JAVA_HOME/bin/keytool -export -alias gemfire -storepass $CERT_PASSWORD -file $SECURITY_DIR/gemfire.cer -keystore $SECURITY_DIR/keystore.jks

$JAVA_HOME/bin/keytool  -noprompt -import -v -trustcacerts -alias gemfire -file $SECURITY_DIR/gemfire.cer -keystore $SECURITY_DIR/truststore.jks -keypass $CERT_PASSWORD -storepass $CERT_PASSWORD


#$JAVA_HOME/bin/keytool -genkey \
#-alias self \
#-dname "CN=trusted" \
#-keypass $CERT_PASSWORD \
#-validity $CERT_VALIDITY_DAYS \
#-keystore $SECURITY_DIR/trusted.keystore \
#-storepass $CERT_PASSWORD \
#-keyalg EC \
#-storetype JKS

chmod 600 $SECURITY_DIR/*.keystore
chmod 600 $SECURITY_DIR/*.jks

echo Generated $SECURITY_DIR/trusted.keystore

echo "ssl-keystore=$SECURITY_DIR/keystore.jks" > $SECURITY_DIR/gfsecurity.properties
echo "ssl-keystore-password=$CERT_PASSWORD" >> $SECURITY_DIR/gfsecurity.properties
echo "ssl-truststore=$SECURITY_DIR/truststore.jks" >> $SECURITY_DIR/gfsecurity.properties
echo "ssl-ciphers=TLS_RSA_WITH_AES_128_GCM_SHA256" >> $SECURITY_DIR/gfsecurity.properties
echo "ssl-require-authentication=true" >> $SECURITY_DIR/gfsecurity.properties
echo "ssl-protocols=TLSv1.2" >> $SECURITY_DIR/gfsecurity.properties

echo "ssl-keystore-type=jks" >> $SECURITY_DIR/gfsecurity.properties

echo "ssl-truststore-password=$CERT_PASSWORD" >> $SECURITY_DIR/gfsecurity.properties
echo "ssl-enabled-components=$SSL_ENABLED_COMPONENTS" >> $SECURITY_DIR/gfsecurity.properties
echo "#ssl-endpoint-identification-enabled=true" >> $SECURITY_DIR/gfsecurity.properties


echo Created gfsecurity profile file
ls $SECURITY_DIR/gfsecurity.properties

echo NOTE: You will need to copy the $SECURITY_DIR/keystore.jks and $SECURITY_DIR/truststore.jks to each member \
on current cluster and REMOTE cluster. See script remoteSyncScripts.sh
