using System;
using System.Collections.Generic;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Serialization;
using Imani.Solutions.Core.API.Util;

namespace Apache.Geode.Core.HTTP.API
{
    public class GeodeClient
    {
        private string urlRoot;
        private IHttp http;

        public GeodeClient(string urlRoot,IHttp http)
        {
            this.urlRoot = urlRoot;
            this.http = http;
        }

        public GeodeClient(string urlRoot)
        :this(urlRoot,new Http())
        {
        }

        public GeodeClient(string urlRoot, String userName,char[] password)
        :this(urlRoot, new Http(userName,password,null))
        {
        }

        public static GeodeClient Connect()
        {
            var conf = new ConfigSettings();

            string rootUrl = conf.GetProperty("HTTP_ROOT_URL");

            string username = conf.GetProperty("USERNAME");
            char[] password = conf.GetPropertyPassword("PASSWORD");
            string domain = null;
            return new GeodeClient(rootUrl, new Http(username,password,domain));
        }

        public IRegion<K,V> GetRegion<K,V>(string regionName)
        {
           return new Region<K,V>(regionName,urlRoot,http, new JsonSerde<V>());
        }

        public IQueryService<T> GetQueryService<T>()
        {
           return new QueryService<T>(urlRoot, http,  new JsonSerde<ICollection<T>>());
        }

        public IFunctionService<I,O> GetFunctionService<I,O>(string functionName)
        {
            return  new FunctionService<I,O>(urlRoot,
            functionName,
             http,  
             new JsonSerde<ICollection<O>>(),
             new JsonSerde<I>());
        }
    }
}