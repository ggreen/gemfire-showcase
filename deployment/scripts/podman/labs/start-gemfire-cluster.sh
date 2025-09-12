
podman network create gemfire-cache --driver bridge

# Run Locator
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf-locator --network=gemfire-cache -p 10334:10334 -p 1099:1099 -p 7070:7070 gemfire/gemfire:10.1-jdk21 gfsh start locator --name=locator1 --jmx-manager-hostname-for-clients=gf-locator --hostname-for-clients=gf-locator --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s



until podman exec -it  gf-locator  gfsh -e "connect --jmx-manager=gf-locator[1099]" >/dev/null 2>&1; do
  echo "Waiting for locator to start..."
  sleep 2
done
echo "Locator is up"


# Configure PDX
podman exec -it gf-locator gfsh -e "connect --jmx-manager=gf-locator[1099]" -e "configure pdx --read-serialized=true --disk-store"
# Run Cache Server
#podman run -d  -e 'ACCEPT_TERMS=y' --rm --name gf-server1 --network=gemfire-cache -p 40404:40404 gemfire/gemfire:10.1-jdk21 gfsh start server --name=server1 --locators=gf-locator\[10334\]
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf-server1 --network=gemfire-cache -p 40404:40404 -p 7080:7080 gemfire/gemfire:10.1-jdk21 gfsh start server --name=server1 --locators=gf-locator\[10334\] --hostname-for-clients=gf-server1 --start-rest-api=true --http-service-port=7080 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777  --J=-Duser.timezone=America/New_York --J=-Dgemfire.prometheus.metrics.interval=15s

