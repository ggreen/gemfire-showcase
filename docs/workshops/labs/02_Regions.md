

Start GemFire Locator and Cache Server

Pre-requisite

- Mac/Linux 
- Podman Desktop 1.20 or higher 
- Java 17 - brew install openjdk@17 
- Apache Maven 3.9.1 + (ex: brew install maven@3.9)
- Curl (Ex: brew install curl)
- Wget (ex: brew install wget)

Run the following script

```shell
./deployment/scripts/podman/labs/start-gemfire-cluster.sh
```


# Introduction to `gfsh` (GemFire Shell)

GemFire provides a command-line tool called **`gfsh` (GemFire Shell)**.  
It is used to manage, configure, and interact with a GemFire cluster. With `gfsh`, you can:

- Start and stop locators and servers
- Create and manage regions
- Perform data operations (put, get, query, remove)
- Monitor and troubleshoot the system

---

## **Starting `gfsh`**

`gfsh` is included in your GemFire installation.  
To start the shell, navigate to the GemFire `bin` directory and run:

Start Gfsh in a container

```shell
deployment/scripts/podman/gfsh.sh
```

Once started, you’ll see a prompt like:

gfsh>


---

## **Connecting to a Locator**

Before interacting with the cluster, connect `gfsh` to a running **locator**:

```gfsh
connect --locator=gf-locator[10334]
```

If the connection is successful, you will see:

Successfully connected to: JMX Manager [host=gf-locator, port=1099]



---

## **Cluster Member Operations**

### **1. List All Members**

To list all members (locators and servers) connected to the cluster:

```gfsh
list members
```


**Example Output**:

```shell
Member Name | Host | Process ID
locator-1 | local | 12345
server-1 | local | 23456
server-2 | local | 34567
```



---

## **Region Data Operations**

Assume we have a region named `exampleRegion`.  
If it doesn’t exist, create it first:

```gfsh
create region --name=exampleRegion --type=PARTITION
```

---

### **2. Put Data into a Region**

```gfsh
put --region=exampleRegion --key=1 --value='{"name":"John","age":30}'
```

**Example**:

```gfsh
put --region=exampleRegion --key=2 --value='{"name":"Jane","age":25}'
```



---

### **3. Get Data from a Region**

```shell
get --region=exampleRegion --key=1
```



**Output**:

```shell
Result      : true
Key Class   : java.lang.String
Key         : 1
Value Class : java.lang.String
Value       : "{\"name\":\"John\",\"age\":30}"
```

---


### **5. Remove Data**


```shell
remove --region=exampleRegion --key=1
```


**Output**:

```
Result    : true
Key Class : java.lang.String
Key       : 1
```


---

### **6. Destroy a Region**

If you want to delete the region completely:

```gfsh
destroy region --name=exampleRegion
```



---

## **Useful Tips**

- Use `help` inside `gfsh` to see a list of available commands:
  help


- Use `hint` for command suggestions:
  hint


- Press **TAB** for auto-completion of commands.

---

## **Summary of Basic Commands**

| Action                | Command Example                                                |
|----------------------|----------------------------------------------------------------|
| Connect to locator   | `connect --locator=localhost[10334]`                           |
| List members         | `list members`                                                |
| Create region        | `create region --name=exampleRegion --type=REPLICATE`          |
| Put data             | `put --region=exampleRegion --key=1 --value='{"name":"John"}'` |
| Get data             | `get --region=exampleRegion --key=1`                           |
| Query data           | `query --query="SELECT * FROM /exampleRegion"`                 |
| Remove data          | `remove --region=exampleRegion --key=1`                        |
| Destroy region       | `destroy region --name=exampleRegion`                          |

---

## **References**

- [GemFire Documentation](gemfire.dev)
- [GemFire gfsh Command Reference](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire/10-1/gf/tools_modules-gfsh-quick_ref_commands_by_area.html)