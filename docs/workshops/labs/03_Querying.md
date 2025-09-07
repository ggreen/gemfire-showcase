# Introduction to Querying in GemFire

GemFire provides a powerful **Object Query Language (OQL)** that allows you to query and analyze data stored in regions.  
This guide introduces:

- Configuring **PDX serialization** for query support
- Using the **REST API** to insert data
- Writing OQL queries for filtering and aggregation

---


## **Connecting to a Locator**

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

## **1. Configuring PDX for Queries**

To enable queries across the cluster, you need to enable **PDX serialization** in your GemFire configuration.  
This ensures that GemFire can deserialize and index fields properly during queries.

The following command been executed as part of the startup `gfsh` enable PDX:

```gfsh
configure pdx --read-serialized=true --disk-store
```

---

## **2. Create the Employee Region**

Create a region named `Employee`:

```gfsh
create region --name=Employee --type=PARTITION
```

---

## **3. Using the REST API to Insert Data**

GemFire's REST API provides an easy way to interact with data in regions.  
Assume the GemFire REST service is running at:

```shell
open http://localhost:7080/gemfire-api
```



### **Example JSON Payload**

Each employee record includes:

- `firstName`
- `lastName`
- `employeeId`
- `department`
- `salary`

### **cURL Examples**

**Insert Employee 1**

```shell
curl -X PUT  -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","employeeId":"E001","department":"Engineering","salary":85000}' http://localhost:7080/gemfire-api/v1/Employee/E001
```


**Insert Employee 2**

```shell
curl -X PUT -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Smith","employeeId":"E002","department":"Engineering","salary":90000}' http://localhost:7080/gemfire-api/v1/Employee/E002
```


**Insert Employee 3**

curl -X PUT -H "Content-Type: application/json" -d '{"firstName":"Alice","lastName":"Brown","employeeId":"E003","department":"HR","salary":75000}' http://localhost:7080/gemfire-api/v1/Employee/E003

---

## **4. Querying Data with OQL**

Once data is inserted, you can run queries in `gfsh` or through the REST API.

---

### **4.1 Query with where condition**

To list all employees in the **Engineering** department:

In gfsh 

```gfsh
query --query="SELECT * FROM /Employee WHERE department = 'Engineering'"
```


**Example Output**:

```
Result : true
Rows : 2
Result : [
{firstName=John, lastName=Doe, employeeId=E001, department=Engineering, salary=85000},
{firstName=Jane, lastName=Smith, employeeId=E002, department=Engineering, salary=90000}
]
```


Finding employees with a salary above a certain amount:

```shell
query --query="SELECT * FROM /Employee WHERE salary < 90000"
```

Select specific fields

```shell
query --query="SELECT employeeId, lastName FROM /Employee WHERE salary < 90000"
```

---

### **4.2 Calculate the Average Salary**

To calculate the average salary across all employees:

```gfsh
query --query="SELECT AVG(salary) FROM /Employee"
```



**Example Output**:

```
Result : true
Rows : 1
Result : [83333.33]
```
Select max and min salary

```gfsh
query --query="SELECT MAX(salary) as emax, MIN(salary) as emin FROM /Employee"
```

---

To calculate the average salary **by department**, use `GROUP BY`:

```gfsh
query --query="SELECT department, AVG(salary) FROM /Employee GROUP BY department"
```



**Example Output**:

```
Result : true
Rows : 2
Result : [
{department=Engineering, AVG(salary)=87500},
{department=HR, AVG(salary)=75000}
]
```

Select Employee with the Max Salary

```shell
query --query="select * from /Employee e where e.salary in (SELECT MAX(emp.salary) FROM /Employee emp)"
```

***********************
Using like

```shell
query --query="select * from /Employee where firstName like 'J%'"
```


Using set

```shell
query --query="select distinct firstName from /Employee where employeeId in SET('E002','E003')"
```

Using Object Methods

```shell
query --query="select distinct firstName, firstName.length() as len from /Employee"
```

```shell
query --query="select count(*) from /Employee where firstName.length() < 5"
```

Create index

```shell
create index --name=Employee --expression=lastName --region=/Employee
```

```shell
query --query="<trace> select * from /Employee where lastName = 'Smith'"
```

***********************


---

## **5. Querying via REST API**

You can also run queries using the REST API.

**Example: Query Engineering Employees**

WHERE department = ''Engineering''

```shell
curl -X 'GET' \
  'http://localhost:7080/gemfire-api/v1/queries/adhoc?q=SELECT%20%2A%20FROM%20%2FEmployee%20WHERE%20department%20%3D%20%27Engineering%27' \
  -H 'accept: application/json;charset=UTF-8'
  ```


**Example: Average Salary**

```shell
curl -X 'GET' \
  'http://localhost:7080/gemfire-api/v1/queries/adhoc?q=SELECT%20AVG%28salary%29%20FROM%20%2FEmployee' \
  -H 'accept: application/json;charset=UTF-8'
```


---

## **6. Summary of Key Steps**

| Step | Command / Endpoint                                                  | Description |
|-------|---------------------------------------------------------------------|-------------|
| Enable PDX | `configure pdx --read-serialized=true`                              | Enables serialization for queries |
| Create Region | `create region --name=Employee --type=REPLICATE`                    | Creates the Employee region |
| Insert Data | `POST /gemfire-api/v1/Employee/{id}`                                | Inserts records into Employee region |
| Query by Department | `SELECT * FROM /Employee WHERE department='Engineering'`            | Lists employees in Engineering |
| Average Salary | `SELECT AVG(salary) FROM /Employee`                                 | Calculates overall average salary |
| Grouped Average Salary | `SELECT department, AVG(salary) FROM /Employee GROUP BY department` | Average salary by department |

---

## **References**

- [GemFire Documentation](https://geode.apache.org/docs/)
- [REST API Guide](https://geode.apache.org/docs/guide/latest/tools_modules/rest_api/rest_api.html)
- [OQL Query Language](https://geode.apache.org/docs/guide/latest/developing/oql_reference.html)







Ask ChatGPT
