# Apache Geode HTTP Dotnet API

This project contains a Dotnet Core API for [Apache Geode](https://geode.apache.org/).
The client library using the Apache Geode [HTTP REST API](https://geode.apache.org/docs/guide/114/rest_apps/rest_api_reference.html).






## Start a Apache Geode

```shell
start locator --name=locator1


start server --name=server1  --start-rest-api=true --http-service-bind-address=localhost --J=-Dgemfire.http-service-port=7071
```



```shell
create region --name=test --type=PARTITION
```

```shell
open http://localhost:7071/geode/swagger-ui.html
```



dotnet add apache-geode-http-dotnet-api.csproj package NSwag.AspNetCore


See https://github.com/RicoSuter/NSwag/wiki/CommandLine

dotnet add package NSwag.CodeGeneration.CSharp --version 13.15.7
