namespace Apache.Geode.Core.HTTP.API
{
    public interface IRegion<K, V>
    {
        public V this[K key]
        {
            get;
            set;
        }

        string Name { get; }
    }
}