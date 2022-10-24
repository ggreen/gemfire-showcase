using System;
using System.Text.Json.Serialization;

namespace Apache.Geode.Core.HTTP.API.Domain
{
    public class FunctionArgTypedValue
    {
        [JsonPropertyName("@type")]
        public string ArgTypeName{ get; set;}

        [JsonPropertyName("@value")]
        public string ArgValue{ get; set;}

      
        public FunctionArgTypedValue()
        {

        }
        public FunctionArgTypedValue(string argTypeName,string argValue)
        {
            this.ArgTypeName = argTypeName;
            this.ArgValue = argValue;
            
        }
    }
}