namespace Apache.Geode.Core.HTTP.API.Test
{
    public class Account
    {
        public string Id {get; set;}

        public string Name{ get; set;}
        
        public string Notes{ get; set;}

        public Location Location{get; set;}

        public override string ToString()
        {
            return $"Account [Id={Id},Name={Name},Notes={Notes}, Location={Location} ]";
        }   
    }
}