./deployment/local/gemfire/start.sh


```shell
curl -X POST http://localhost:9090/gemfire-vectordb/v1/indexes -H "Content-Type: application/json" -d '{"name": "quickstart"}'
```
```shell
curl http://localhost:9090/gemfire-vectordb/v1/indexes/quickstart
```


```shell
curl -X POST http://localhost:9090/gemfire-vectordb/v1/indexes/quickstart/embeddings \
  -H "Content-Type: application/json" \
  -d '[
  {
    "key": "embedding1",
    "vector": [0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1]
  },
  {
    "key": "embedding2",
    "vector": [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
  },
  {
    "key": "embedding3",
    "vector": [1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1]
  },
  {
    "key": "embedding4",
    "vector": [0.2, 0.4, 0.8, 1.6, 3.2, 3.2, 1.6, 0.8, 0.4, 0.2]
  },
  {
    "key": "embedding5",
    "vector": [1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0, 1.0, 2.0]
  }
]'
```


Testing

```shell
curl http://localhost:9090/gemfire-vectordb/v1/indexes/quickstart
```

Search

```shell
curl -X POST http://localhost:9090/gemfire-vectordb/v1/indexes/quickstart/query -H "Content-Type: application/json" -d '{"vector": [0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1], "top-k": 3}'
```