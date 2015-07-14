package com.joshcummings.java8.lambda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionUtils {
	public static <T> Collection<T> filter(Collection<T> coll, Predicate<T> p) {
		Collection<T> filtered = new ArrayList<>();
		for ( T obj : coll ) {
			if ( p.test(obj) ) {
				filtered.add(obj);
			}
		}
		return filtered;
	}
	
	public static <T,U> Collection<U> map(Collection<T> coll, Function<T,U> f) {
		// 1. Create a new collection (call it mapped)
		Collection<U> mapped = new ArrayList<U>();
		
		// 2. For loop through the original collection
		for ( T element : coll ) {
			// 3. For each element, call the apply method on f
			U u = f.apply(element);
			
			// 4. Add the result of apply to "mapped"
			mapped.add(u);
		}
		
		// 5. Return mapped
		return mapped;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
