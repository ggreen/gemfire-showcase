GEODE_HOME=/Users/devtools/repositories/IMDG/geode/apache-geode-1.13.1

$GEODE_HOME/bin/gfsh -e "connect --user=admin --password=admin" -e "destroy region --name=/Location" 

$GEODE_HOME/bin/gfsh -e "connect --user=admin --password=admin" -e "destroy region --name=/Account"


gcloud beta container --project "gregoryg-playground" clusters delete "cluster-1"