cd $GEMFIRE_HOME/bin

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=locator1 --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1"

curl http://localhost:7777/metrics
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1 --locators=localhost[10334] --server-port=1880 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8590"
curl http://localhost:7778/metrics

$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --name=Account --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --name=UserAccount --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --name=Location --type=PARTITION --enable-statistics=true"