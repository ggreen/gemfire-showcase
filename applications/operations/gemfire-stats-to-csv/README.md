# GemFire Stats To CSV

This application print useful [GemFire](https://gemfire.dev/) statistics to a CSV file.

This application only prints the [Most useful statistics](https://community.broadcom.com/education/blogs/migration-user/2021/07/01/the-most-useful-statistics-for-troubleshooting-vmware-gemfire-deployments) for troubleshooting  GemFire Deployments.

## Getting Started


| Property          | Notes                                                                                                                      |
|-------------------|----------------------------------------------------------------------------------------------------------------------------|
| csv.output.file   | The path of the output CSV file                                                                                            |
| stats.input.path  | The input directory containing the GemFire statistic files. Note the application searches for nested stats in sub-folders. |
| -stats.day.filter | Allow print stats that match the provide day filter to the CSV file. Date Format is MM/dd/yyyy.                            |


Example run

```shell
java -jar applications/operations/gemfire-stats-to-csv/build/libs/gemfire-stats-to-csv-0.0.1-SNAPSHOT.jar --csv.output.file=/tmp/gf.csv --stats.input.path --stats.input.path=applications/operations/gemfire-stats-to-csv/src/test/resources/server1-2members.gfs --stats.day.filter="6/23/2025"
```


