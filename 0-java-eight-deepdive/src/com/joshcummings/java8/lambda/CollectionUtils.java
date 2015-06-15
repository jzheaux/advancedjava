package com.joshcummings.java8.lambda;

import java.util.ArrayList;
import java.util.Collection;
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
}
