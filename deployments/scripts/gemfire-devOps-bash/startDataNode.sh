#!/bin/bash
# Starts a cache server data node on the local server
source ./setenv.sh

if [ ! -f $SECURITY_DIR/gfsecurity.properties ]
then
  ./selfSignCert.sh
fi

source ./common.library

mkdir -p $WORK_DIR/$CS_NM

if [ ! -f "$SECURITY_DIR/gfsecurity.properties" ]
then

   echo ERROR: $SECURITY_DIR/gfsecurity.properties does not exist. See selfSignCert.sh or create it manually  1>&2

   exit 1
fi

if [ -z $SECURITY_USERNAME ]
then
  echo "Missing setenv property SECURITY_USERNAME"
  exit 1
fi

if [ -z $MAX_CONNECTIONS ]; then
  echo "Missing setenv property MAX_CONNECTIONS"
  exit 1
fi

export CACHE_XML_OPTION=

if [ -n $CACHE_XML_FILE ]
then
   export CACHE_XML_OPTION="--cache-xml-file=$CACHE_XML_FILE"
fi


if [ $# -eq 1 ]; then
    echo "Starting second data node"
    export CS_PORT=20100
    export CS_TCP_PORT=20002
    export CS_MEMBERSHIP_PORT_RANGE=20801-20810
    export CS_NM=server2
    export CS_GW_RECIEVER_PORT=25000-25010
    export CS_REMOTE_DEBUGGING_PORT=24000
fi

#}" ] && [ "{$REMOTE_DEBUGGING}" -eq "true" ]
if [ -n $REMOTE_DEBUGGING ]
then
  if [ "$REMOTE_DEBUGGING" = "true" ]
  then
      export REMOTE_DEBUGGING_OPTION="--J=-Xdebug --J=-Xrunjdwp:server=y,suspend=n,transport=dt_socket,address=$CS_REMOTE_DEBUGGING_PORT"
  fi
fi

#---------------------------------
# Print out for debugging
echo DISTRIBUTED_ID=$DISTRIBUTED_ID
echo REMOTE_DISTRIBUTED_ID=$REMOTE_DISTRIBUTED_ID

# Start Cache Server
nohup $GEMFIRE_HOME/bin/gfsh -e  "start server --name="$MEMBER_HOST_NM"_$CS_NM --locators=$LOCATORS $CACHE_XML_OPTION --initial-heap=$DATA_NODE_HEAP_SIZE --max-heap=$DATA_NODE_HEAP_SIZE --dir=$WORK_DIR/$CS_NM --use-cluster-configuration=$ENABLE_CLUSTER_CONFIGURATION --server-port=$CS_PORT   --log-level=$LOG_LEVEL --start-rest-api=true --J=-Dgemfire.conserve-sockets=false --J=-Dgemfire.max-connections=$MAX_CONNECTIONS --J=-Dgemfire.tcp-port=$CS_TCP_PORT --statistic-archive-file=$MEMBER_STAT_FILE --J=-Dgemfire.Query.VERBOSE=true --J=-Dgemfire.QueryService.allowUntrustedMethodInvocation=true --J=-Dgemfire.membership-port-range=$CS_MEMBERSHIP_PORT_RANGE --hostname-for-clients=$MEMBER_HOST_NM --J=-Dgemfire.bind-address=$MEMBER_HOST_NM --J=-Dgemfire.http-service-bind-address=$MEMBER_HOST_NM  --J=-Dgemfire.enable-time-statistics=$ENABLE_TIME_STATISTICS --J=-Dgemfire.http-service-port=$REST_HTTP_PORT   --J=-Dgemfire.log-disk-space-limit=$LOG_DISK_LIMIT_MB --J=-Dgemfire.log-file-size-limit=$LOG_FILE_LIMIT_MB --disable-classloader-isolation=false  --classpath="$EXT_CLASSPATH" --J=-Dgemfire.security-manager=$SECURITY_MANAGER  --J=-Dconfig.properties=$SECURITY_USER_PROPERTIES --user=$SECURITY_USERNAME --password=$SECURITY_PASSWORD --J=-Dgemfire.statistic-archive-file=$DN_STATS_FILE   --J=-D-gemfire.statistic-sampling-enabled=true  --J=-Dgemfire.archive-disk-space-limit=$STAT_DISK_LIMIT_MB  --J=-Dgemfire.archive-file-size-limit=$STAT_FILE_LIMIT_MB   --J=-Dgemfire.mcast-port=0  --security-properties-file=$SECURITY_DIR/gfsecurity.properties  $REMOTE_DEBUGGING_OPTION  --J=-Dgemfire.redundancy-zone=$REDUNDANCY_ZONE --J=-Dgemfire.prometheus.metrics.port=$PROMETHEUS_DATA_NODE_PORT" >$WORK_DIR/$CS_NM/"$CS_NM"_datanode_start.log 2>&1 &


# --J=-Xloggc:$WORK_DIR/$CS_NM/gc.log  --J=-Xlog:gc --J=-Xlog:safepoint --J=-Xlog:gc* --J=-Xlog:task*=debug  --J=-XX:+UnlockDiagnosticVMOptions --J=-XX:+DisableExplicitGC --J=-XX:+UseG1GC

if [ -n $REMOTE_DEBUGGING ]
then
  if [ "$REMOTE_DEBUGGING" = "true" ]
  then
      echo REMOTE_DEBUGGING_OPTION=$REMOTE_DEBUGGING_OPTION
  fi
fi


if [ "$IS_CONTAINER" == "true" ]; then
  echo Data node runnning

  while true
  do
    sleep 3s
    GEODE_PIDS=`ps -ef | grep java | awk '{print $1}'`
    if [ -z "$GEODE_PIDS" ]
    then
      echo "Data node stopped"
      exit 1
    fi
  done

fi
