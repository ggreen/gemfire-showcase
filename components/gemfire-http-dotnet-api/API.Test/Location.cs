namespace Apache.Geode.Core.HTTP.API.Test
{
    public class Location
    {


        public Location(){
        }

        public Location(string address, string city, string state, int zipPostalCode, string country)
        {
            this.Address = address;
            this.City = city;
            this.State = state;
            this.ZipPostcode = zipPostalCode;
            this.Country = country;
        }

        public string Address{get; set;}
        public string City{get; set;}
        public string State{get; set;}
        public int ZipPostcode{get; set;}
        public string Country{get; set;}


        public override string ToString()
        {
            return $" Location[Address:{Address}, City:{City}, State:{State}, ZipPostcode:{ZipPostcode}, Country:{Country}] ";
        }
    }
}