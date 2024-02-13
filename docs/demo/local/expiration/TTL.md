# GemFire Data Expiration Example

See [online document example](https://docs.vmware.com/en/VMware-GemFire/10.0/gf/developing-expiration-configuring_data_expiration.html).

## Local Testing

[Download GemFire](https://network.pivotal.io/products/pivotal-gemfire)


Start GemFire

```shell
cd $GEMFIRE_HOME/bin
./gfsh -e "start locator --name=locator" 
./gfsh -e "connect" -e "configure pdx --read-serialized=true --disk-store"
./gfsh -e "start server --name=server --locators=localhost[10334]"
```


Create regions

```shell
./gfsh -e "create region --name=test --type=PARTITION"
./gfsh -e "create region --name=Account --entry-idle-time-expiration=5 --entry-idle-time-expiration-action=DESTROY --enable-statistics=true --type=PARTITION"  
```

Start Application

See [example account service](examples/spring-data-gemfire/applications/account-service)


```shell
curl -X 'POST' \
  'http://localhost:8080/accounts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "hello",
  "name": "world"
}'
```

Within 5 seconds this return results

```shell
curl -X 'GET' \
  'http://localhost:8080/accounts?id=hello' \
  -H 'accept: */*'
```

