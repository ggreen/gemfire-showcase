docker run -it -e 'ACCEPT_TERMS=y' --network=gemfire-cache  gemfire/gemfire:10.0.3 gfsh


#-e "connect --jmx-manager=gf-locator[1099]" -e "list members"
#gemfire/gemfire:10.0.3