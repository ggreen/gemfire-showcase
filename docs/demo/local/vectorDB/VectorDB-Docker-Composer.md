

```shell
mkdir -p runtime/ai/vector
cd runtime/ai/vector
```

```shell
curl -O https://raw.githubusercontent.com/gemfire/gemfire-examples/main/docker-compose.yaml
docker compose up
```

Wait a minute or so for the cache server port to be available


Create VectorDB index: quickstart

```shell
curl -X POST http://localhost:7071/gemfire-vectordb/v1/indexes -H "Content-Type: application/json" -d '{"name": "quickstart"}'
```

View Index

```shell
curl http://localhost:7071/gemfire-vectordb/v1/indexes/quickstart
```



Troubleshooting cache server 

```shell
docker exec -it gemfire-cluster-gemfire-server-0-1 bash
cd /data
cat server-0.log
```


Shutdown cluster


```shell
cd runtime/ai/vector
docker compose down
```