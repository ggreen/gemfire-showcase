using System.Collections.Generic;
using Apache.Geode.Core.HTTP.API.Test;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Serialization;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Apache.Geode.Core.HTTP.API;
using Apache.Geode.Core.HTTP.API.Domain;
using System.Text.Json;

namespace Apache.Geode.Core.HTTP.Test
{
    [TestClass]
    public class FunctionServiceTest
    {
        private Mock<IHttp> http;
        private Mock<ISerde<ICollection<Account>, string>> resultSerde;
        // private Mock<ISerde<FunctionArgTypedValue[]>, string>> inputSerde;

        private FunctionService<FunctionArgTypedValue[],Account> subject;
        private string urlRoot = "http://localhost:18080";
        private string functionName = "myfunction";
        private  Mock<ISerde<FunctionArgTypedValue[], string>> inputSerde;

        [TestInitialize]
        public void InitializeRegionTest()
        {
            http=  new Mock<IHttp>();
            inputSerde = new Mock<ISerde<FunctionArgTypedValue[], string>>();
            resultSerde = new Mock<ISerde<ICollection<Account>,string>>();
            subject = new FunctionService<FunctionArgTypedValue[],Account>(urlRoot,functionName,http.Object,resultSerde.Object,inputSerde.Object);
        }

        [TestMethod]
        public void Execute()
        {

            Account expectedObject = new Account();
            string jsonText = JsonSerializer.Serialize(expectedObject);
            HttpResponse json = new HttpResponse(100,$"[{jsonText}]");
            http.Setup( h => h.Post(It.IsAny<string>(),It.IsAny<string>(),It.IsAny<string>())).Returns(json);

            ICollection<Account> expectedList = new LinkedList<Account>();
            expectedList.Add(expectedObject);
            resultSerde.Setup( s => s.Deserialize(It.IsAny<string>())).Returns(expectedList);


            FunctionArgTypedValue[] args = new FunctionArgTypedValue[1];
            ICollection<Account> actual  = subject.Execute(args);

            Assert.IsNotNull(actual);
            Assert.AreEqual(1,actual.Count);
            expectedObject.Id = "test";

            var enumerator = actual.GetEnumerator();
            enumerator.MoveNext();

            Assert.AreEqual(expectedObject.Id,enumerator.Current.Id);
        }
    }
}