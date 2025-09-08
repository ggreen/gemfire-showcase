
podman network create gemfire-cache --driver bridge

# Run Locator
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf1-gl-locator --network=gemfire-cache -p 10334:10334 -p 1099:1099 -p 7777:7777 gemfire/gemfire:10.1-jdk21 gfsh   start locator --name=gf1-gl-locator --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=gf1-gl-locator --bind-address=gf1-gl-locator  --J=-Dgemfire.prometheus.metrics.interval=15s  --http-service-port=7071 --J=-Dgemfire.jmx-manager-port=1099 --max-heap=250m --initial-heap=250m --J=-Dgemfire.distributed-system-id=1 --J=-Duser.timezone=America/New_York --J=-Dgemfire.remote-locators=gf2-gl-locator\[10002\]


until curl --output /dev/null --silent --head --fail curl http://localhost:7777/metrics; do
    printf '.'
    sleep 5
done

sleep 10

# Configure PDX
podman exec -it gf1-gl-locator gfsh -e "connect --jmx-manager=gf1-gl-locator[1099]" -e "configure pdx --read-serialized=true --disk-store"
# Run Cache Server

podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf1-gl-server --network=gemfire-cache -p 7778:7778 -p 40404:40404 gemfire/gemfire:10.1-jdk21 gfsh start server --name=gf1-gl-server --locators=gf1-gl-locator\[10334\] --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=gf1-gl-server --bind-address=gf1-gl-server  --J=-Dgemfire.prometheus.metrics.interval=15s  --use-cluster-configuration=true --server-port=10101 --max-heap=1g   --initial-heap=1g --J=-Duser.timezone=America/New_York  --J=-Dgemfire.distributed-system-id=1

until curl --output /dev/null --silent --head --fail curl http://localhost:7778/metrics; do
    printf '.'
    sleep 5
done

# Setup GemFire Account Region

podman exec -it gf1-gl-locator gfsh -e "connect --jmx-manager=gf1-gl-locator[1099]" -e "create gateway-receiver" -e  "create gateway-sender --id=Account_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"

# Setup GemFire Location Region
podman exec -it gf1-gl-locator gfsh -e "connect --jmx-manager=gf1-gl-locator[1099]" -e "create gateway-sender --id=test_Sender_to_2 --parallel=true  --remote-distributed-system-id=2 --enable-persistence=true --enable-batch-conflation=true"


podman exec -it gf1-gl-locator gfsh -e "connect --jmx-manager=gf1-gl-locator[1099]" -e "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_2"


podman exec -it gf1-gl-locator gfsh -e "connect --jmx-manager=gf1-gl-locator[1099]" -e "create region --name=health1 --type=PARTITION"

podman exec -it gf1-gl-locator gfsh -e "connect --jmx-manager=gf1-gl-locator[1099]" -e "create region --name=test --type=PARTITION --gateway-sender-id=test_Sender_to_2"

# ------ Cluster 2 -------------------

podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf2-gl-locator --network=gemfire-cache -p 10002:10002 -p 1098:1098 -p 9778:9778 gemfire/gemfire:10.1-jdk21 gfsh   start locator --name=gf2-gl-locator --enable-cluster-configuration=true --connect=false  --http-service-port=7072 --J=-Dgemfire.tcp-port=11111 --J=-Dgemfire.jmx-manager-port=1098 --max-heap=250m --initial-heap=250m --bind-address=gf2-gl-locator --hostname-for-clients=gf2-gl-locator  --jmx-manager-hostname-for-clients=gf2-gl-locator --http-service-bind-address=gf2-gl-locator  --J=-Dgemfire.distributed-system-id=2 --port=10002 --J=-Dgemfire.remote-locators=gf2-gl-locator\[10334\] --J=-Dgemfire.prometheus.metrics.port=9778 --J=-Dgemfire.prometheus.metrics.host=gf2-gl-locator --J=-Dgemfire.prometheus.metrics.interval=15s --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Duser.timezone=Europe/London

until curl --output /dev/null --silent --head --fail curl http://localhost:9778/metrics; do
    printf '.'
    sleep 5
done

sleep 10

podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]" -e "configure pdx --read-serialized=true --disk-store"



podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf2-gl-server --network=gemfire-cache -p 10102:10102 -p 9779:9779 gemfire/gemfire:10.1-jdk21 gfsh start server --name=gf2-gl-server --use-cluster-configuration=true --server-port=10102   --locators=gf2-gl-locator\[10002\] --max-heap=1g   --initial-heap=1g  --bind-address=gf2-gl-server --hostname-for-clients=gf2-gl-server  --jmx-manager-hostname-for-clients=gf2-gl-server --http-service-bind-address=gf2-gl-server  --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.prometheus.metrics.port=9779 --J=-Dgemfire.prometheus.metrics.host=gf2-gl-server --J=-Dgemfire.prometheus.metrics.interval=15s --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Duser.timezone=Europe/London

until curl --output /dev/null --silent --head --fail curl http://localhost:9779/metrics; do
    printf '.'
    sleep 5
done

podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]" -e "create gateway-receiver"


podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]"  -e  "create gateway-sender --id=Account_Sender_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"

podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]" -e "create gateway-sender --id=test_to_1 --parallel=true  --remote-distributed-system-id=1 --enable-persistence=true --enable-batch-conflation=true"


podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]" -e "create region --name=Account --type=PARTITION --gateway-sender-id=Account_Sender_to_1"

podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]" -e "create region --name=health2 --type=PARTITION"



podman exec -it gf2-gl-locator gfsh -e "connect --jmx-manager=gf2-gl-locator[1098]" -e "create region --name=test --type=PARTITION --gateway-sender-id=test_to_1"


## Shutdown


