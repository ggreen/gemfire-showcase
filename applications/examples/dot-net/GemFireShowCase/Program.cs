using GemFire.Client;

namespace GemFireExample
{
  public class Employee : IPdxSerializable
  {
      public  string Id { get; set; }
      public  string FirstName { get; set; }
      public  string LastName { get; set; }

        public void FromData(IPdxReader reader)
        {
            Id = reader.ReadString("Id");
            FirstName = reader.ReadString("FirstName");
            LastName = reader.ReadString("LastName");
        }

        public void ToData(IPdxWriter writer)
        {
            writer.WriteString("Id", Id);
            writer.WriteString("FirstName", FirstName);
            writer.WriteString("LastName", LastName);
        }


       public override string ToString()
      {
          return $"Id: {Id}, FirstName: {FirstName}, LastName: {LastName}";
      }
    }

 class Program
  {
    static void Main(string[] args)
    {

      Console.WriteLine("Connecting to GeFire");

      var cache = new CacheFactory()
      .AddLocator("127.0.0.1", 10334)
          .Create("local");

      cache.TypeRegistry.RegisterPdxType(CreateDeserializable);

      var regionFactory = cache.CreateRegionFactory(RegionShortcut.PROXY);
      var region = regionFactory.Create<String, Employee>("Employee");

      Console.WriteLine("Storing id and username in the region");
      
      Employee emp1 = new Employee();
      emp1.Id = "rtimmons";
      emp1.FirstName = "Robert";
      emp1.LastName = "Timmons";

      
      Employee emp2 = new Employee();
      emp2.Id = "jimani";
      emp2.FirstName = "Josiah";
      emp2.LastName = "Imani";


      Console.WriteLine("Getting the user info from the region");
      region.Put(emp1.Id, emp1);
       region.Put(emp2.Id, emp2);


      var user1 = region.Get(emp1.Id);
      var user2 = region.Get(emp2.Id);
      

      Console.WriteLine("user1: "+user1+" \n user2:"+user2);

      var queryService = cache.GetQueryService();

      var query = queryService.NewQuery<Employee>(
          "SELECT * FROM /Employee WHERE FirstName > 'Josiah'"
      );

      var results = query.Execute();

      Console.WriteLine("****** result="+results.First().FirstName);



      cache.Close();
    }

        private static IPdxSerializable CreateDeserializable()
        {
            return new Employee();
        }
    }
}