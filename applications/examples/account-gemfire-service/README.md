# Account GemFire Service


This project provides an example REST Service for Create, Read, Update and Delete (CRUD)
operations on [Tanzu GemFire](https://tanzu.vmware.com/gemfire).

It uses [Jetty](https://jetty.org/index.html) as an embedded Web Server.


## Building Source Code

Set up Maven Repository user credentials
See https://gemfire.dev/quickstart/java/

```shell
export BROADCOM_MAVEN_USERNAME=$HARBOR_USER
export BROADCOM_GEMFIRE_MAVEN_PASSWORD=$HARBOR_PASSWORD
```

Building source code

```shell
gradle  build
```

# Running the REST Web Service

## Start GemFire

Set the **GEMFIRE_HOME** environment variable.

Start and startup GemFire using the provided script

```shell
./deployment/local/gemfire/start.sh
```


## Web Account

```shell
export LOCATORS=host1[10334]
java -jar applications/examples/account-gemfire-service/build/libs/account-gemfire-service-*.jar --FACTORY_REPOSITORY=showcase.gemfire.account.repository.AccountGemFireRepository
```


## Testing


Save Account Data

```shell
curl "http://localhost:8080/accounts" \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": "4",
  "name": "User 1",
  "contact" : { 
    "email" : "gideon@gemfire.dev",
    "mobilePhone" : "555-555-5555"
  }, 
  "workAddress" : {
    "streetAddress" : "875 Howard Street 5th Floor",
    "city" : "San Francisco",
    "state" : "CA",
    "zip" : "9410"
  }
}'
```

Read Account

```shell
curl http://localhost:8080/accounts/4
```