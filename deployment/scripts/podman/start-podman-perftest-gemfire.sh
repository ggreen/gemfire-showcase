
podman network create gemfire-cache --driver bridge

# Run Locator
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf-locator --network=gemfire-cache -p 10334:10334 -p 1099:1099 -p 7070:7070 gemfire/gemfire:10.0.3 gfsh start locator --name=locator1

sleep 10
# Configure PDX
podman exec -it gf-locator gfsh -e "connect --jmx-manager=gf-locator[1099]" -e "configure pdx --read-serialized=true --disk-store"
# Run Cache Server
#podman run -d  -e 'ACCEPT_TERMS=y' --rm --name gf-server1 --network=gemfire-cache -p 40404:40404 gemfire/gemfire:10.0.3 gfsh start server --name=server1 --locators=gf-locator\[10334\]
podman run -d -e 'ACCEPT_TERMS=y' --rm --name gf-server1 --network=gemfire-cache -p 40404:40404 gemfire/gemfire:10.0.3 gfsh start server --name=server1 --locators=gf-locator\[10334\]
sleep 15
# Setup GemFire Account Region
podman exec -it gf-locator gfsh -e "connect --jmx-manager=gf-locator[1099]" -e "create region --name=Account --type=PARTITION  --enable-statistics=true"

# Setup GemFire Location Region
podman exec -it gf-locator gfsh -e "connect --jmx-manager=gf-locator[1099]" -e "create region --name=Location --type=PARTITION --enable-statistics=true"

podman exec -it gf-locator gfsh -e "connect --jmx-manager=gf-locator[1099]" -e "create region --name=test --type=PARTITION  --enable-statistics=true"


