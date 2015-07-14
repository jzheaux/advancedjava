package com.joshcummings.java8.lambda;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Optional;


public class UnreliableMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	
	private SecureRandom cacheMiss = new SecureRandom();
	
	private Optional<V> unreliableGet(Object key) {
		V value = super.get(key);
		return value == null || cacheMiss.nextInt(100) > 95 ? Optional.empty() : Optional.of(value);
	}
	
	@Override
	public V put(K key, V value) {
		V old = this.get(key);
		super.put(key, value);
		return old;
	}
	
	@Override
	public V get(Object key) {
		return get(key, 3);
	}
	
	public V get(Object key, int retries) {
		return retries == 0 ? null : this.unreliableGet(key).orElseGet(() -> get(key, retries - 1));
	}
	
	public static void main(String[] args) {
		UnreliableMap<Integer, Integer> map = new UnreliableMap<>();
		map.put(1, 1);
		map.put(2, 1);
		map.put(3, 2);
		map.put(4, 3);
		map.put(5, 5);
		
		for ( int i = 0; i < 10000000; i++ ) {
			if ( map.get(3,5) == null ) {
				System.out.printf("Iteration #%d was null%n", i);
			}
		}
	}
}
