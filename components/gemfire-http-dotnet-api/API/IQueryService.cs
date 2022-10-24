using System.Collections.Generic;
using Apache.Geode.Core.HTTP.API.Test;

namespace Apache.Geode.Core.HTTP.API
{
    public interface IQueryService<T>
    {
        ICollection<T> Query(string oql);
    }
}