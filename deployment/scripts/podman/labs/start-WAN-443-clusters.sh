podman network create gemfire-cache --driver bridge

# ------ Cluster 1 -------------------
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf1-locator --network=gemfire-cache  gemfire/gemfire:10.1-jdk21 gfsh start locator --name=gf1-locator  --enable-cluster-configuration=true --connect=false --port=10001 --http-service-port=7071 --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m  --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.remote-locators="gf2-locator[10002]" --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s

until podman exec -it  gf1-locator  gfsh -e "connect --locator=gf1-locator[10001]" >/dev/null 2>&1; do
  echo "Waiting for locator to start..."
  sleep 2
done
echo "Locator is up"




podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf1-server --network=gemfire-cache gemfire/gemfire:10.1-jdk21 gfsh start server --name=gf1-server --use-cluster-configuration=true --server-port=10101   --locators="gf1-locator[10001]" --max-heap=1g   --initial-heap=1g --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s


podman exec -it gf1-locator gfsh -e "connect --locator=gf1-locator[10001]" -e "configure pdx --read-serialized=true --disk-store"


# ------ Cluster 2 -------------------
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf2-locator --network=gemfire-cache  gemfire/gemfire:10.1-jdk21 gfsh start locator --name=gf2-locator --enable-cluster-configuration=true --connect=false  --http-service-port=7072 --J=-Dgemfire.tcp-port=11111 --port=10002 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m  --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.remote-locators="gf1-locator[10001]" --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s

until podman exec -it  gf2-locator  gfsh -e "connect --locator=gf2-locator[10002]" >/dev/null 2>&1; do
  echo "Waiting for locator to start..."
  sleep 2
done
echo "Locator is up"


podman exec -it gf2-locator gfsh -e "connect --locator=gf2-locator[10002]"  -e  "configure pdx --read-serialized=true --disk-store"


podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf2-server --network=gemfire-cache gemfire/gemfire:10.1-jdk21 gfsh start server --name=gf2-server --use-cluster-configuration=true --server-port=10102   --locators="gf2-locator[10002]" --max-heap=1g   --initial-heap=1g   --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s


podman exec -it gf1-locator gfsh -e "connect --locator=gf1-locator[10001]"  -e  "create gateway-receiver --start-port=443 --end-port=443" -e  "create gateway-sender --id=Account_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"




podman exec -it gf1-locator gfsh -e "connect --locator=gf1-locator[10001]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_2"


podman exec -it gf1-locator gfsh -e "connect --locator=gf1-locator[10001]"  -e  "create region --name=health1 --type=PARTITION"

sleep 10

podman exec -it gf2-locator gfsh -e "connect --locator=gf2-locator[10002]" --e  "create gateway-receiver --start-port=443 --end-port=443"  -e  "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"

podman exec -it gf2-locator gfsh -e "connect --locator=gf2-locator[10002]"  -e  "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_1"

podman exec -it gf2-locator gfsh -e "connect --locator=gf2-locator[10002]"  -e  "create region --name=health2 --type=PARTITION"