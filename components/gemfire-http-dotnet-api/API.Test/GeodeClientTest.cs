using System;
using System.Collections.Generic;
using Apache.Geode.Core.HTTP.API;
using Apache.Geode.Core.HTTP.API.Domain;
using Apache.Geode.Core.HTTP.API.Test;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Util;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Apache.Geode.Core.HTTP.Test
{
    //[TestClass]
    public class GeodeClientTest
    {

        private GeodeClient subject;
        private readonly string urlRoot = "http://localhost:7071";

        private Mock<IHttp> http;

        [TestInitialize]
        public void InitializeGeodeClientTest()
        {
            http = new Mock<IHttp>();
            subject = new GeodeClient(urlRoot,http.Object);
        }

        [TestMethod]
        public void QuerService()
        {
            Assert.IsNotNull(subject.GetQueryService<Account>());
        }

        [TestMethod]
        public void GetRegion()
        {
            string regionName = "test";
            IRegion<string,Account> region = subject.GetRegion<string,Account>(regionName);
            Assert.IsNotNull(region);
        }


        [TestMethod]
        public void Integration_Performance_Test()
        {
            string cyrptionKey ="PLEASECHANGEMETHISISJUSTATEST";

            var cryption = new Cryption(cyrptionKey);
                                                                http://localhost:9090/geode/v1/customers?keys=jdoe%40vmware.com&op=PUT
            Environment.SetEnvironmentVariable("HTTP_ROOT_URL","http://localhost:9090/geode/v1");
            Environment.SetEnvironmentVariable("CRYPTION_KEY",cyrptionKey);
            Environment.SetEnvironmentVariable("USERNAME","admin");
            Environment.SetEnvironmentVariable("PASSWORD",$"{Cryption.CRYPTION_PREFIX}{cryption.EncryptText("admin")}");
            
            var subject = GeodeClient.Connect();

            var expected = new Account();
            expected.Id = "test";

            var key = "1";
            var testRegion = subject.GetRegion<string,Account>("test");

            int executionCount = 10000;
            var putWatch = System.Diagnostics.Stopwatch.StartNew();
            for (int i=0; i < executionCount; i++)
            {
                testRegion[key] = expected;
            }
            putWatch.Stop();
            var elapsedMs = putWatch.ElapsedMilliseconds;
            Console.WriteLine($"PUT time {elapsedMs/executionCount}/ms ");


            Account actual;
            var getWatch = System.Diagnostics.Stopwatch.StartNew();
            for (int i=0; i < executionCount; i++)
            {
                actual = testRegion[key];
            }
            getWatch.Stop();
            var getElapsedMs = getWatch.ElapsedMilliseconds;
            Console.WriteLine($"GET time {getElapsedMs/executionCount}/ms ");

        }

        [TestMethod]
        public void IntegrationRegion_Test()
        {
            string cyrptionKey ="PLEASECHANGEMETHISISJUSTATEST";

            var cryption = new Cryption(cyrptionKey);
            Environment.SetEnvironmentVariable("HTTP_ROOT_URL","http://localhost:9090/geode/v1");
            Environment.SetEnvironmentVariable("CRYPTION_KEY",cyrptionKey);
            Environment.SetEnvironmentVariable("USERNAME","admin");
            Environment.SetEnvironmentVariable("PASSWORD",$"{Cryption.CRYPTION_PREFIX}{cryption.EncryptText("admin")}");
            
            var subject = GeodeClient.Connect();

            //You can use native data object save a JSON format using PDX serializer
            var expected = new Account();
            expected.Id = "test";
            expected.Name = "VMware GemFire";
            expected.Notes = "This framework is powered by Power Apache Geode";
            expected.Location = new Location("123 Street","LA","CA",12345,"US");


            //Regions are like a database table with key/value format
            var key = "1";
            var testRegion = subject.GetRegion<string,Account>("test");

            //region PUT by key string,
            testRegion[key] = expected;

            //Get value based on key string
            var actual = testRegion[key];
            
            Assert.AreEqual(expected.Id,actual.Id);


            //Query Service
            var queryService = subject.GetQueryService<Account>();

            //Usign the Object Query Language (very similar to SQL)
            string oql = $"select * from /{testRegion.Name} where Id = {expected.Id}";

            ICollection<Account> actualList = queryService.Query(oql);

            Assert.IsNotNull(actualList);


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
            
            /*ICollection<Account[]> luceneResults = functionService.Execute(args);    

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
            */

        }

    }
    //----------------------------


    //     [TestMethod]
    //     public void Integration_Open_Bank_Region_Test()
    //     {
    //         Environment.SetEnvironmentVariable("HTTP_ROOT_URL","http://localhost:18080/geode/v1");
            
    //         var subject = GeodeClient.Connect();

    //         //You can use native data object save a JSON format using PDX serializer
    //         var expected = new Account();
    //         expected.Id = "test";
    //         expected.Name = "VMware GemFire";
    //         expected.Notes = "This framework is powered by Power Apache Geode";
    //         expected.Location = new Location("123 Street","LA","CA",12345,"US");


    //         //Regions are like a database table with key/value format
    //         var key = "1";
    //         var testRegion = subject.GetRegion<string,Account>("test");

    //         //region PUT by key string,
    //         testRegion[key] = expected;

    //         //Get value based on key string
    //         var actual = testRegion[key];
            
    //         Assert.AreEqual(expected.Id,actual.Id);


    //         //Query Service
    //         var queryService = subject.GetQueryService<Account>();

    //         //Usign the Object Query Language (very similar to SQL)
    //         string oql = $"select * from /{testRegion.Name} where Id = {expected.Id}";

    //         ICollection<Account> actualList = queryService.Query(oql);

    //         Assert.IsNotNull(actualList);


    //         //Each Function has a unique
    //         string functionName = "SimpleLuceneSearchFunction";

    //         //Function Service
    //         IFunctionService<FunctionArgTypedValue[],Account[]> functionService = subject.GetFunctionService<FunctionArgTypedValue[],Account[]>(
    //             functionName);
            

    //         //The Input Arguments are unique for each function
    //         string luceneQuery = "Id:t*";
    //         string defaultField = "Id";


    //         FunctionArgTypedValue[] args = new FunctionArgTypedValue[4];
    //         args[0] = new FunctionArgTypedValue("String","testIndex");
    //         args[1] = new FunctionArgTypedValue("String",testRegion.Name);
    //         args[2] = new FunctionArgTypedValue("String",luceneQuery);
    //         args[3] = new FunctionArgTypedValue("String",defaultField);

    //         //Each function is executed in a distributed fashion
    //         //Developers have control on where the functions execute
    //         ICollection<Account[]> luceneResults = functionService.Execute(args);    

    //         //Results are aggregated from each distributed execution
    //         Assert.IsNotNull(luceneResults); 
    //         Assert.IsTrue(luceneResults.Count > 0); 


    //         foreach(Account[] results in luceneResults)
    //         {
    //            foreach(Account domain in results)
    //            {
    //                Console.WriteLine($"DOMAIN: {domain}");
    //            }
    //         }

    //     }

    // }

    
}