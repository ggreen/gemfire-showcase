cd $GEMFIRE_HOME/bin

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=locator1-2members --port=10334 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7977 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true  --initial-heap=512m --max-heap=512m  --J=-Dgemfire.start-rest-api=true --J=-Dgemfire.statistic-archive-file=locator1-2members.gfs   --J=-D-gemfire.statistic-sampling-enabled=true "

curl http://127.0.0.1:7777/metrics
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
$GEMFIRE_HOME/bin/gfsh -e "start server --name=server1-2members --locators=127.0.0.1[10334] --initial-heap=1g --max-heap=1g --server-port=2882 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7978 --J=-Dgemfire.prometheus.metrics.host=127.0.0.1 --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8590 --J=-Dgemfire.enable-management-rest-service=true --J=-Dgemfire.enable-cluster-configuration=true --J=-XX:+AlwaysPreTouch --start-rest-api=true --J=-Dgemfire.statistic-archive-file=server1-2members.gfs   --J=-D-gemfire.statistic-sampling-enabled=true "
curl http://127.0.0.1:7778/metrics

$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=Account --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=UserAccount --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=Location --type=PARTITION --enable-statistics=true"
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --skip-if-exists=true --name=test --type=PARTITION --enable-statistics=true"

## simpleIndex uses default Lucene StandardAnalyzer
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=simpleIndex --region=example-search-region --field=firstName,lastName"

## analyzerIndex uses both the default StandardAnalyzer and the KeywordAnalyzer
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=analyzerIndex --region=example-search-region --field=lastName,email --analyzer=DEFAULT,org.apache.lucene.analysis.core.KeywordAnalyzer"

## nestedObjectIndex will index on nested objects or collection objects
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create lucene index --name=nestedObjectIndex --region=example-search-region --field=contacts.phoneNumbers --serializer=org.apache.geode.cache.lucene.FlatFormatSerializer"

$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --skip-if-exists=true --name=example-search-region --type=PARTITION --enable-statistics=true"

#Number of seconds for expiration = 1 hour
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "create region --skip-if-exists=true --name=Paging --type=PARTITION  --eviction-entry-count=10000 --eviction-action=local-destroy --entry-time-to-live-expiration=3600 --enable-statistics=true"




