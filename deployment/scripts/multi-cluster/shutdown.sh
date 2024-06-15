export GEMFIRE_HOME=/Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-10.0.2

$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10001]" -e "shutdown --include-locators"
$GEMFIRE_HOME/bin/gfsh -e "connect --locator=localhost[10002]" -e "shutdown --include-locators"

sleep 10
rm -rf gf1-locator
rm -rf gf1-server
rm -rf gf2-locator
rm -rf gf2-server