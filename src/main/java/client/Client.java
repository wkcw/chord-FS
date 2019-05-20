package client;


public interface Client<K, V> {

    public V get();
    public void put(K key, V Value);

}
