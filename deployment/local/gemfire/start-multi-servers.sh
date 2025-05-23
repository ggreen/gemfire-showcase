cd $GEMFIRE_HOME/bin
rm -rf locator*
rm -rf server*

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=locator1 --port=10334 --max-heap=250m --initial-heap=250m --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1"

curl http://localhost:7777/metrics
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1 --max-heap=500m --initial-heap=500m --locators=localhost[10334] --server-port=1880 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8590" &
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server2 --max-heap=500m --initial-heap=500m --locators=localhost[10334] --server-port=1881 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7798 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8591"
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server3 --max-heap=500m --initial-heap=500m --locators=localhost[10334] --server-port=1882 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7799 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8592"

$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=Account --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=UserAccount --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=Location --type=PARTITION --enable-statistics=true"

## simpleIndex uses default Lucene StandardAnalyzer
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=simpleIndex --region=example-search-region --field=firstName,lastName"

## analyzerIndex uses both the default StandardAnalyzer and the KeywordAnalyzer
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=analyzerIndex --region=example-search-region --field=lastName,email --analyzer=DEFAULT,org.apache.lucene.analysis.core.KeywordAnalyzer"

## nestedObjectIndex will index on nested objects or collection objects
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=nestedObjectIndex --region=example-search-region --field=contacts.phoneNumbers --serializer=org.apache.geode.cache.lucene.FlatFormatSerializer"

$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --skip-if-exists=true --name=example-search-region --type=PARTITION --enable-statistics=true"

#Number of seconds for expiration = 1 hour
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --skip-if-exists=true --name=Paging --type=PARTITION  --eviction-entry-count=10000 --eviction-action=local-destroy --entry-time-to-live-expiration=3600 --enable-statistics=true"




