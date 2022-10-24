using System;
using System.Collections.Generic;
using Apache.Geode.Core.HTTP.API.Test;
using Imani.Solutions.Core.API.NET;
using Imani.Solutions.Core.API.Serialization;

namespace Apache.Geode.Core.HTTP.API
{
    public class FunctionService<I, O> : IFunctionService<I, O>
    {
        private string functionName;
        private string urlRoot;
        private IHttp http;
        private ISerde<ICollection<O>, string> resultSerde;

        private ISerde<I,string> inputSerde;
        private static readonly string contentType = "application/json";

        public FunctionService(string urlRoot, string functionName,IHttp http, ISerde<ICollection<O>, string> resultsSerde,ISerde<I,string> inputSerde)
        {
            this.functionName = functionName;
            this.urlRoot = urlRoot;
            this.http = http;
            this.inputSerde = inputSerde;
            this.resultSerde = resultsSerde;
        }

        public ICollection<O> Execute(I args)
        {
            //curl -X POST "http://localhost:18080/geode/v1/functions/SimpleLuceneSearchFunction" -H "accept: application/json;charset=UTF-8" -H "Content-Type: application/json" -d "[ { \"@type\": \"string\", \"@value\": \"testIndex\" }, { \"@type\": \"string\", \"@value\": \"test\" }, { \"@type\": \"string\", \"@value\": \"Id:t*\" }, { \"@type\": \"string\", \"@value\": \"Id\" } ]"
            string payload = inputSerde.Serialize(args);
            var response =  http.Post(urlRoot+$"/functions/{functionName}",payload,contentType);
            return resultSerde.Deserialize(response.Body);
        }
    }
}