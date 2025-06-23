🔧 3. Common G1 GC Tuning Suggestions

| Symptom                        | 	Possible Cause	                    | Tuning Suggestions                                                             |
|--------------------------------|-------------------------------------|--------------------------------------------------------------------------------|
| Long GC pauses	                | Too few GC threads                  | 	-XX:ParallelGCThreads=N and -XX:ConcGCThreads=M                               |                                                                                |
| Old Gen growing too fast	      | Promotion failure, short tenuring   | 	-XX:MaxTenuringThreshold, check object lifetimes                              |
| Frequent Full GCs              | 	Insufficient heap or fragmentation | 	Increase -Xmx, review humongous allocations                                   |
| High allocation rates          | 	Inefficient Eden sizing	           | Adjust -XX:G1NewSizePercent, -XX:G1MaxNewSizePercent                           |
| Too many humongous allocations | 	Large object allocation            | 	Break up large data structures or increase region size (-XX:G1HeapRegionSize) |