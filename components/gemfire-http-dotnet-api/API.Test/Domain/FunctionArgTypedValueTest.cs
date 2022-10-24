using System;
using System.Text.Json;
using Apache.Geode.Core.HTTP.API.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace apache_geode_http_dotnet_api.API.Test.Domain
{
   [TestClass]
   public class FunctionArgTypedValueTest
   {
       
     [TestMethod]
     public void Json()
     {
         FunctionArgTypedValue[] args = new FunctionArgTypedValue[1];

         args[0] = new FunctionArgTypedValue();
         args[0].ArgTypeName = "hello";
         args[0].ArgValue = "world";

         var output = JsonSerializer.Serialize(args);
         Console.WriteLine(output);


     }
   }
}