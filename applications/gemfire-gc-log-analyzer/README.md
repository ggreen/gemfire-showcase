# gemfire-gc-log-analyzer

This application provides a report of raw Java Garbage Collection logs


## Getting Started

Running the applications

```shell
java -jar applications/gemfire-gc-log-analyzer/build/libs/gemfire-gc-log-analyzer-0.0.1-SNAPSHOT.jar --gc.file.pattern="*gc.log*" --reporting.directory=$REPORT_DIR --input.gc.logs.directory=$ANALYZER_DIR
```
