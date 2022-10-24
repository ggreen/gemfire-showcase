using System.Collections.Generic;
using Apache.Geode.Core.HTTP.API.Test;

namespace Apache.Geode.Core.HTTP.API
{
    public interface IFunctionService<I, O>
    {
        ICollection<O> Execute(I args);
    }
}