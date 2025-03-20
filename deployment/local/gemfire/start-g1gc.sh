cd $GEMFIRE_HOME/bin

# see https://docs.oracle.com/en/java/javase/14/docs/specs/man/java.html#enable-logging-with-the-jvm-unified-logging-framework
# also run java  -Xlog:help
# Files are rotated by default with up to 5 rotated files of target size 20 MB
$GEMFIRE_HOME/bin/gfsh -e "start locator --name=gclocator1 --port=10334   --J=-XX:-UseZGC  --J=-XX:+UseG1GC --J=-Xlog:gc=info:file=gc.txt   --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7777 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1 --J=-Dgemfire.statistic-archive-file=locator.gfs"

curl http://localhost:7777/metrics
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"



$GEMFIRE_HOME/bin/gfsh -e "start server --name=gcserver1 --locators=localhost[10334] --initial-heap=2g --max-heap=2g --J=-Xlog:gc=info:file=gc.txt --J=-XX:-UseZGC --J=-XX:+UseG1GC --server-port=1880 --J=-Dgemfire.prometheus.metrics.emission=Default --J=-Dgemfire.prometheus.metrics.port=7778 --J=-Dgemfire.prometheus.metrics.host=localhost --J=-Dgemfire.prometheus.metrics.interval=15s --bind-address=127.0.0.1  --http-service-port=8590 --J=-Dgemfire.statistic-archive-file=gcserver1.gfs"

curl http://localhost:7778/metrics

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




