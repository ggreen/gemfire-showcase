using System;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Serialization;

namespace Apache.Geode.Core.HTTP.API
{
    public class Region<K,V> : IRegion<K,V>
    {
        private IHttp http;
        private string urlRoot;

        private string regionName;

        private ISerde<V, string> serde;
        private readonly bool isStringTypeValue;
        private static readonly string jsonContentType = "application/json";

  

        public Region(string regionName, string urlRoot, IHttp http, ISerde<V, string> serde )
        {
            this.regionName = regionName;
            this.urlRoot = urlRoot;
            this.http = http;
            this.serde = serde;

            this.isStringTypeValue = typeof(V) == typeof(String);
        }

       public string Name {
            get{
                return regionName;
            }
        }

        public V this[K key] 
        { 
            get
            { 
                var response =  http.Get(urlRoot+$"/{regionName}/{key}");
                
                if(!isStringTypeValue)
                    return serde.Deserialize(response.Body);

                //(T)Convert.ChangeType(base.Value, typeof(T));
                return (V)Convert.ChangeType(response.Body,typeof(V));
            } 

            set
            {
                String payload;

                if(!isStringTypeValue)
                {
                    payload = serde.Serialize(value);
                } 
                else
                {
                    payload = value.ToString();
                }

                http.Put(urlRoot+$"/{regionName}?keys={key}&op=PUT",payload,jsonContentType);
            } 
        }
    }
}