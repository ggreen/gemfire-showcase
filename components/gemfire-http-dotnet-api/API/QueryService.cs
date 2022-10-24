using System.Collections.Generic;
using Apache.Geode.Core.HTTP.API.Test;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Serialization;

namespace Apache.Geode.Core.HTTP.API
{
    public class QueryService<T> : IQueryService<T>
    {
        private string urlRoot;
        private IHttp http;
        private ISerde<ICollection<T>, string> serde;

        public QueryService(string urlRoot, IHttp http, ISerde<ICollection<T>,string> serde)
        {
            this.urlRoot = urlRoot;
            this.http = http;
            this.serde = serde;
        }

        public ICollection<T> Query(string oql)
        {
            //http://localhost:18080/geode/v1/queries/adhoc?q=select%20*%20from%20%2Ftest
            var response =  http.Get(urlRoot+$"/queries/adhoc?q={oql}");
            return serde.Deserialize(response.Body);
        }
    }
}