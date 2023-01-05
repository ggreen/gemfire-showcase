# Apache Geode Health Shell App

This spring shell based application provides
utility to assist with the assessment of Apache Geode
based cluster deployments.


 Report included Key Data Point graphs
 Exposes shell commands to generate specific charts/reports based on statistic files

## Start Up
Starting the app shell

        java -jar target/dataTx-gART-shell-app-<version>.jar


## User guide

Example usages

        shell:>chart-cpu-above-threshold --input-file-path-dir ./stats --out-file-image-path /Users/ggreen/cpu.png --day-date 7/4/2019

        shell:>chart-jvm-avg-memory-above-threshold --input-file-path-dir ./stats --memory-percentage 50 --out-file-image-path ./jvmAvg.png



        shell:>help
        …

### AVAILABLE COMMANDS

**Built-In Commands**

Command | Notes
-------- | ----------------
 clear      |  Clear the shell screen. 
 exit, quit |  Exit the shell.
 help       | Display help about available commands.
 script     |  Read and execute commands from a file.
 stacktrace | Display the full stacktrace of the last error.

**Geode Health Commands**

Command             | Notes
------------------- | ----------------
chart-cpu-usage     | Builds a chart of the CPU Usage
chart-jvm-avg-memory-above-threshold | Builds a chart of the AVG JVM Memory Over a Threshold
chart-jvm-max-memory-above-threshold | Builds a chart of the MAX JVM Memory Over a Threshold
chart-par-new-collection-time-threshold | Builds a chart ParNew Collections times over a duration
chart-par-new-collections               | Builds a chart ParNew Garbage Collections
csv                         | Converts statistics to CSV
db-sync                     | Saves stats to database
html-report                 |Builds a complete report based on statistic files


# Command Operations Guide

## db-sync


The db-sync supports storing statistics details to a 
relational database.

Supported Databases

- [PostgresSQL](https://www.postgresql.org/)
- [MySQL](https://www.mysql.com/)
- [H2](https://www.h2database.com/html/main.html)

Example Usage

    db-sync --batch-size 1000 --jdbc-db-type MySQL --day-yyyymmddfilter "2019-08-12" --jdbc-username "user" --jdbc-pasword "password" --stats-file-or-dir-path "/users/ggreen/stats" --stat-type-name "CachePerfStats" --stat-name "creates" --jdbc-url "jdbc:mysql://<host>:<port>/mysql"
 
 
 Parameter          |   Notes
 ------------------ | ---------------------
jdbc-db-type        |  The target database to insert stats. Possible values (H2,MySql, PostgresDB)Ex: MySQL 
jdbc-url                | The database JDBC URL Ex: "jdbc:mysql://<host>:<port>/mysql" 
jdbc-username       |  The database username 
jdbc-pasword        |  The database password. This is optional, do not provide if the database user does not have a password. 
batch-size          |  Batch insert size. Increase this can improve time it take to insert statistics into the database Ex: 1000
stats-file-or-dir-path |  The Apache Geode statistic file. Statistic file are normally have a gfs extension. You can also provide a directory and this command will recursively look for *.gfs files. Ex: "/users/ggreen/stats"
day-yyyymmddfilter  |  The desired capture day with format yyyy-MM-dd. Only statistics on this day will be saved Ex: "2019-08-12" 
stat-type-name          | The name of Apache Geode Statistic to capture. See [Geode Statistics List](https://geode.apache.org/docs/guide/12/reference/statistics/statistics_list.html) for possible values.  Ex: "CachePerfStats" 
stat-name               | The stat name to capture within the *stat-type-name*.  Ex: "creates" 



This command using JPA to automatic created table needed to the 
store the statistics. The table name is **StatEntity**.

The following is an example StatEntity table schema definition 
created in Postgres. 

    CREATE TABLE "public"."statentity"
    (
       id bigint PRIMARY KEY NOT NULL,
       filtertypename varchar(255),
       label varchar(255),
       machine varchar(255),
       statname varchar(255) NOT NULL,
       stattimestamp timestamp NOT NULL,
       statvalue float(19) NOT NULL
    );
    CREATE UNIQUE INDEX statentity_pkey ON "public"."statentity"(id);
    



Note that persisting data to a relationship database is preferred 
versus using the report-html command.

![CPU Usage Stats](docs/grafana-cpu.png)

Graphing tools such as [Grafana](https://grafana.com/).
The following is an example Grafana dashboard definition for LinuxSystemStats stats.


        {
          "annotations": {
            "list": [
              {
                "builtIn": 1,
                "datasource": "-- Grafana --",
                "enable": true,
                "hide": true,
                "iconColor": "rgba(0, 211, 255, 1)",
                "name": "Annotations & Alerts",
                "type": "dashboard"
              }
            ]
          },
          "editable": true,
          "gnetId": null,
          "graphTooltip": 0,
          "id": 11,
          "links": [],
          "panels": [
            {
              "aliasColors": {},
              "bars": false,
              "dashLength": 10,
              "dashes": false,
              "datasource": "PostgreSQL",
              "fill": 1,
              "fillGradient": 0,
              "gridPos": {
                "h": 8,
                "w": 12,
                "x": 0,
                "y": 0
              },
              "id": 4,
              "legend": {
                "avg": false,
                "current": false,
                "max": false,
                "min": false,
                "show": true,
                "total": false,
                "values": false
              },
              "lines": true,
              "linewidth": 1,
              "nullPointMode": "null",
              "options": {
                "dataLinks": []
              },
              "percentage": false,
              "pointradius": 2,
              "points": false,
              "renderer": "flot",
              "seriesOverrides": [],
              "spaceLength": 10,
              "stack": false,
              "steppedLine": false,
              "targets": [
                {
                  "format": "time_series",
                  "group": [],
                  "metricColumn": "statname",
                  "rawQuery": false,
                  "rawSql": "SELECT\n  $__time(time_column),\n  value1\nFROM\n  metric_table\nWHERE\n  $__timeFilter(time_column)\n",
                  "refId": "A",
                  "select": [
                    [
                      {
                        "params": [
                          "statvalue"
                        ],
                        "type": "column"
                      }
                    ]
                  ],
                  "table": "statentity",
                  "timeColumn": "stattimestamp",
                  "timeColumnType": "timestamp",
                  "where": [
                    {
                      "name": "$__timeFilter",
                      "params": [],
                      "type": "macro"
                    },
                    {
                      "datatype": "varchar",
                      "name": "",
                      "params": [
                        "filtertypename",
                        "=",
                        "'LinuxSystemStats'"
                      ],
                      "type": "expression"
                    }
                  ]
                }
              ],
              "thresholds": [],
              "timeFrom": null,
              "timeRegions": [],
              "timeShift": null,
              "title": "LinuxSystemStats CpuActive",
              "tooltip": {
                "shared": true,
                "sort": 0,
                "value_type": "individual"
              },
              "type": "graph",
              "xaxis": {
                "buckets": null,
                "mode": "time",
                "name": null,
                "show": true,
                "values": []
              },
              "yaxes": [
                {
                  "format": "short",
                  "label": null,
                  "logBase": 1,
                  "max": null,
                  "min": null,
                  "show": true
                },
                {
                  "format": "short",
                  "label": null,
                  "logBase": 1,
                  "max": null,
                  "min": null,
                  "show": true
                }
              ],
              "yaxis": {
                "align": false,
                "alignLevel": null
              }
            },
            {
              "aliasColors": {},
              "bars": false,
              "dashLength": 10,
              "dashes": false,
              "datasource": "PostgreSQL",
              "fill": 1,
              "fillGradient": 0,
              "gridPos": {
                "h": 9,
                "w": 12,
                "x": 0,
                "y": 8
              },
              "id": 2,
              "legend": {
                "avg": false,
                "current": false,
                "max": false,
                "min": false,
                "show": true,
                "total": false,
                "values": false
              },
              "lines": true,
              "linewidth": 1,
              "nullPointMode": "null",
              "options": {
                "dataLinks": []
              },
              "percentage": false,
              "pointradius": 2,
              "points": false,
              "renderer": "flot",
              "seriesOverrides": [],
              "spaceLength": 10,
              "stack": false,
              "steppedLine": false,
              "targets": [
                {
                  "format": "time_series",
                  "group": [],
                  "metricColumn": "statname",
                  "rawQuery": false,
                  "rawSql": "SELECT\n  $__time(time_column),\n  value1\nFROM\n  metric_table\nWHERE\n  $__timeFilter(time_column)\n",
                  "refId": "A",
                  "select": [
                    [
                      {
                        "params": [
                          "statvalue"
                        ],
                        "type": "column"
                      }
                    ]
                  ],
                  "table": "statentity",
                  "timeColumn": "stattimestamp",
                  "timeColumnType": "timestamp",
                  "where": [
                    {
                      "name": "$__timeFilter",
                      "params": [],
                      "type": "macro"
                    },
                    {
                      "datatype": "varchar",
                      "name": "",
                      "params": [
                        "filtertypename",
                        "=",
                        "'VMGCStats'"
                      ],
                      "type": "expression"
                    }
                  ]
                }
              ],
              "thresholds": [],
              "timeFrom": null,
              "timeRegions": [],
              "timeShift": null,
              "title": "GC Collections",
              "tooltip": {
                "shared": true,
                "sort": 0,
                "value_type": "individual"
              },
              "type": "graph",
              "xaxis": {
                "buckets": null,
                "mode": "time",
                "name": null,
                "show": true,
                "values": []
              },
              "yaxes": [
                {
                  "format": "short",
                  "label": null,
                  "logBase": 1,
                  "max": null,
                  "min": null,
                  "show": true
                },
                {
                  "format": "short",
                  "label": null,
                  "logBase": 1,
                  "max": null,
                  "min": null,
                  "show": true
                }
              ],
              "yaxis": {
                "align": false,
                "alignLevel": null
              }
            }
          ],
          "refresh": false,
          "schemaVersion": 19,
          "style": "dark",
          "tags": [],
          "templating": {
            "list": []
          },
          "time": {
            "from": "2019-05-20T07:17:28.700Z",
            "to": "2019-05-20T07:17:53.646Z"
          },
          "timepicker": {
            "refresh_intervals": [
              "5s",
              "10s",
              "30s",
              "1m",
              "5m",
              "15m",
              "30m",
              "1h",
              "2h",
              "1d"
            ]
          },
          "timezone": "",
          "title": "Apache Geode Statistics",
          "uid": "NJ7TYsyZk",
          "version": 3
        }     