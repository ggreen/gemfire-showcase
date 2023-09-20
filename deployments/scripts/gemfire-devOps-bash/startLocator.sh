#!/bin/bash
# Starts a locator on the local server

source ./setenv.sh

if [ ! -f $SECURITY_DIR/gfsecurity.properties ]
then
  ./selfSignCert.sh
fi

source ./common.library

if [ -z $SECURITY_DIR ]
then
  echo "Missing setenv property SECURITY_DIR"
  exit 1
fi

if [ -z $CRYPTION_KEY ]
then
  echo "Missing setenv property CRYPTION_KEY"
  exit 1
fi

if [ -z $SECURITY_USERNAME ]
then
  echo "Missing setenv property SECURITY_USERNAME"
  exit 1
fi


if [ -z $SECURITY_USER_PROPERTIES ]
then
  echo "Missing setenv property SECURITY_USER_PROPERTIES"
  exit 1
fi


if [ ! -f "$SECURITY_DIR/gfsecurity.properties" ]
then

   echo ERROR: $SECURITY_DIR/gfsecurity.properties does not exist. See selfSignCert.sh or create it manually  1>&2

   exit 1
fi

#---------------------------------
# Print out for debugging
echo DISTRIBUTED_ID=$DISTRIBUTED_ID
echo REMOTE_DISTRIBUTED_ID=$REMOTE_DISTRIBUTED_ID

mkdir -p $WORK_DIR/$LOCATOR_NM/cluster_config

# start locator
$GEMFIRE_HOME/bin/gfsh -e "start locator --name="$LOCATOR_HOST"_$LOCATOR_NM --bind-address=$LOCATOR_HOST --J=-Djava.rmi.server.hostname=$LOCATOR_HOST --hostname-for-clients=$LOCATOR_HOST --J="-Dgemfire.jmx-manager-hostname-for-clients=$LOCATOR_HOST"  --locators=$LOCATORS --port=$LOCATOR_PORT --dir=$WORK_DIR/$LOCATOR_NM --log-level=$LOG_LEVEL --initial-heap=$LOCATOR_HEAP_SIZE --max-heap=$LOCATOR_HEAP_SIZE --connect=false --J=-Dgemfire.jmx-manager-port=$JMX_MANAGER_PORT --J=-Dgemfire.jmx-manager-start=true  --J=-Dgemfire.enable-time-statistics=$ENABLE_TIME_STATISTICS --J=-Dgemfire.remote-locators=$REMOTE_LOCATORS --J=-Dgemfire.distributed-system-id=$DISTRIBUTED_ID  --J=-Dgemfire.conserve-sockets=false --J=-Dgemfire.tcp-port=$LOC_TCP_PORT --J=-Dgemfire.membership-port-range=$LOC_MEMBERSHIP_PORT_RANGE   --enable-cluster-configuration=$ENABLE_CLUSTER_CONFIGURATION --cluster-config-dir=$WORK_DIR/$LOCATOR_NM/cluster_config --http-service-port=$PULSE_HTTP_PORT  --J=-Xlog:gc --J=-Xlog:safepoint --J=-Xlog:gc* --J=-Xlog:task*=debug  --security-properties-file=$SECURITY_DIR/gfsecurity.properties --J=-Dgemfire.log-disk-space-limit=$LOG_DISK_LIMIT_MB --J=-Dgemfire.log-file-size-limit=$LOG_FILE_LIMIT_MB  --include-system-classpath=true --J=-Dgemfire.security-manager=$SECURITY_MANAGER --J=-Dgemfire.security-username=$SECURITY_USERNAME --J=-Dgemfire.security-password=$SECURITY_PASSWORD  --J=-Dconfig.properties=$SECURITY_USER_PROPERTIES --J=-Dgemfire.statistic-archive-file=$LOC_STATS_FILE  --J=-D-gemfire.statistic-sampling-enabled=true --J=-Dgemfire.archive-disk-space-limit=$STAT_DISK_LIMIT_MB --J=-Dgemfire.archive-file-size-limit=$STAT_FILE_LIMIT_MB --J=-Dlog4j.configurationFile=$LOG4J2_XML_FILE "

#echo $GEMFIRE_HOME/bin/gfsh -e "start locator --name="$LOCATOR_HOST"_$LOCATOR_NM"

if [ "$IS_CONTAINER" = "true" ]; then
  echo "Locator runnning"

  while true
  do
    sleep 3s
    GEODE_PIDS=`ps -ef | grep java | awk '{print $1}'`
    if [ -z "$GEODE_PIDS" ]
    then
      echo "Locator stopped";
      exit 1
    fi
  done

fi
