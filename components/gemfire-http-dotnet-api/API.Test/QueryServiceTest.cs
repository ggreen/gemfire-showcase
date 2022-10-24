using System.Collections.Generic;
using System.Text.Json;
using Apache.Geode.Core.HTTP.API;
using Apache.Geode.Core.HTTP.API.Test;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Serialization;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Apache.Geode.Core.HTTP.API.Test
{
    [TestClass]
    public class QueryServiceTest
    {
        private Mock<IHttp> http;
        private Mock<ISerde<ICollection<Account>, string>> serde;
        private string urlRoot = "http://localhost:18080";
        private QueryService<Account> subject;

        [TestInitialize]
        public void InitializeRegionTest()
        {
            http=  new Mock<IHttp>();
            serde = new Mock<ISerde<ICollection<Account>,string>>();
            subject = new QueryService<Account>(urlRoot,http.Object,serde.Object);
        }

        [TestMethod]
        public void Query()
        {

            Account expectedObject = new Account();
            string jsonText = JsonSerializer.Serialize(expectedObject);
            HttpResponse json = new HttpResponse(100,$"[{jsonText}]");
            http.Setup( h => h.Get(It.IsAny<string>())).Returns(json);

            ICollection<Account> expectedList = new LinkedList<Account>();
            expectedList.Add(expectedObject);
            serde.Setup( s => s.Deserialize(It.IsAny<string>())).Returns(expectedList);

            string oql = "select * from /test";


            ICollection<Account> actual  = subject.Query(oql);

            Assert.IsNotNull(actual);
            Assert.AreEqual(1,actual.Count);
            expectedObject.Id = "test";

            var enumerator = actual.GetEnumerator();
            enumerator.MoveNext();

            Assert.AreEqual(expectedObject.Id,enumerator.Current.Id);
        }
    }
}