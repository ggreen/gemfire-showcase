# Apache Geode HTTP Dotnet API

This project contains a [Dotnet Core](https://dotnet.microsoft.com/en-us/download) API for [GemFire](https://tanzu.vmware.com/gemfire).
The client library using the [GemFire HTTP REST API](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-rest_apps-chapter_overview.html).






## Start a GemFire Cluster


Open the [GemFire Gfsh](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.10/tgf/GUID-tools_modules-gfsh-chapter_overview.html) CLI

```shell
cd $GEMFIRE_HOME/bin
./gfsh
```

In Gfsh Start a [Locator](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.10/tgf/GUID-configuring-running-running_the_locator.html) process

```shell
start locator --name=locator1  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 
```

In Gfsh Configure [GemFire PDX](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.15/tgf/GUID-developing-data_serialization-gemfire_pdx_serialization.html) 

```shell
configure gemFireJson --read-serialized=true --disk-store
```

In Gfsh Start Server that stored the data

```shell
start server --name=server1   --locators=127.0.0.1[10334]  --initial-heap=2g  --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1 --start-rest-api=true --J=-Dgemfire.http-service-port=7071
```

In Gfsh create a [GemFire Region](https://docs.vmware.com/en/VMware-Tanzu-GemFire/9.10/tgf/GUID-developing-region_options-chapter_overview.html) (similar to a database table)

```shell
create region --name=test --type=PARTITION
```

GemFire provides a SWAGGER UI to test the REST API

```shell
open http://localhost:7071/geode/swagger-ui.html
```


# Client Example


Create the wrapper client

```csharp
  string gemFireUrl = "http://localhost:7071/geode/v1";
  var gemfire =new GeodeClient(gemFireUrl);
```

Basic Get/Put Example

```csharp

   //You can use native data object save a JSON format using PDX serializer
     var expected = new Account();
     expected.Id = "test";
     expected.Name = "GemFire";
     expected.Notes = "This framework is powered by Power VMware";
     expected.Location = new Location("123 Street","LA","CA",12345,"US");

     //Regions are like a database table with key/value format
     var key = "1";
     var testRegion = gemfire.GetRegion<string,Account>("test");

     //region PUT by key string,
     testRegion[key] = expected;

     //Get value based on key string
     var actual = testRegion[key];
     
     Assert.AreEqual(expected.Id,actual.Id);
```

Using the Query Service

```csharp
    //Query Service
   var queryService = gemfire.GetQueryService<Account>();

   //Usign the Object Query Language (very similar to SQL)
   string oql = $"select * from /{testRegion.Name} where Id = '{expected.Id}'";

   ICollection<Account> actualList = queryService.Query(oql);

   Assert.IsNotNull(actualList);
   Assert.IsTrue(actualList.Count > 0);
```


Using a Function

```csharp
   var subject = new GeodeClient(gemFireUrl);

   //You can use native data object save a JSON format using PDX serializer
   var expected = new Account();
   expected.Id = "test";
   expected.Name = "VMware GemFire";
   expected.Notes = "This framework is powered by Power Apache Geode";
   expected.Location = new Location("123 Street","LA","CA",12345,"US");


   //Regions are like a database table with key/value format
   var testRegion = subject.GetRegion<string,Account>("test");

   //Each Function has a unique
   string functionName = "SimpleLuceneSearchFunction";

   //Function Service
   IFunctionService<FunctionArgTypedValue[],Account[]> functionService = subject.GetFunctionService<FunctionArgTypedValue[],Account[]>(
       functionName);
   

   //The Input Arguments are unique for each function
   string luceneQuery = "Id:t*";
   string defaultField = "Id";

   FunctionArgTypedValue[] args = new FunctionArgTypedValue[4];
   args[0] = new FunctionArgTypedValue("String","testIndex");
   args[1] = new FunctionArgTypedValue("String",testRegion.Name);
   args[2] = new FunctionArgTypedValue("String",luceneQuery);
   args[3] = new FunctionArgTypedValue("String",defaultField);

   //Each function is executed in a distributed fashion
   //Developers have control on where the functions execute
   
   ICollection<Account[]> luceneResults = functionService.Execute(args);    

   //Results are aggregated from each distributed execution
   Assert.IsNotNull(luceneResults); 
   Assert.IsTrue(luceneResults.Count > 0); 

   foreach(Account[] results in luceneResults)
   {
      foreach(Account domain in results)
      {
          Console.WriteLine($"DOMAIN: {domain}");
      }
   }        

```







