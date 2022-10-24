using Imani.Solutions.Core.API.NET;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Imani.Solutions.Core.API.Serialization;
using System.Text.Json;

namespace Apache.Geode.Core.HTTP.API.Test
{
    [TestClass]
    public class RegionTest
    {
        private Region<string,Account> subject;
        private Mock<IHttp> http;

        private Mock<ISerde<Account,string>> serde;
        private string urlRoot = "http://localhost:8080";
        private string regionName ="test";

        [TestInitialize]
        public void InitializeRegionTest()
        {
            http=  new Mock<IHttp>();
            serde = new Mock<ISerde<Account,string>>();
            subject = new Region<string,Account>(regionName,urlRoot,http.Object,serde.Object);
        }

        [TestMethod]
        public void Name()
        {
            Assert.AreEqual(regionName,subject.Name);
        }

        [TestMethod]
        public void Put()
        {
            string expectedKey = "test";
            Account expectedValue = new Account();
            subject[expectedKey] = expectedValue;

            
            serde.Verify(s => s.Serialize(It.IsAny<Account>()));
            http.Verify( http=> http.Put(It.IsAny<string>(),It.IsAny<string>(),It.IsAny<string>()));
            
        }

           [TestMethod]
        public void Get()
        {
            Account expected = new Account();
            expected.Id = "expected";

            string json = JsonSerializer.Serialize(expected);
            HttpResponse response = new HttpResponse(200,json);
            http.Setup(h => h.Get(It.IsAny<string>())).Returns(response);
            serde.Setup(s => s.Deserialize(It.IsAny<string>())).Returns(expected);
   

            string expectedKey = "test";
            Account actual = subject[expectedKey];
            Assert.IsNotNull(actual);

            Assert.AreEqual(expected.Id, actual.Id);
        }

        [TestMethod]
        public void IsString_DoNotDeserialize()
        {
            Mock<ISerde<string,string>> serde =  new Mock<ISerde<string,string>>();
            string json = "hello";
            HttpResponse response = new HttpResponse(200,json);
            http.Setup(h => h.Get(It.IsAny<string>())).Returns(response);
   

            string expectedKey = "test";
            
            Region<string,string> subject = new Region<string,string>(regionName,urlRoot,http.Object,serde.Object);

              var actual = subject[expectedKey];
            Assert.IsNotNull(actual);

            serde.Verify(s => s.Serialize(It.IsAny<string>()),Times.Never);
            
        }
    }
}