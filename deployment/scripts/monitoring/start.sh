cd $GEMFIRE_HOME/bin

./gfsh -e "start locator --name=locator1 --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1"

curl http://localhost:7777/metrics
./gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
./gfsh -e "start server --name=server1 --locators=localhost[10334] --server-port=1880 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1"
curl http://localhost:7778/metrics