package com.joshcummings.codeplay.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Here, we use ReentrantLock just to demonstrate the additional flexibility we get with the API
 * 
 * We could also have a method like "count all" that syncrhonizes on a common mutex to get the same effect.
 * 
 * @author jzheaux
 *
 */
public class BasicStatsCounter implements StatsCounter {
	private final Map<Integer, AtomicInteger> ageCount = new ConcurrentHashMap<>();
	private final Map<String, AtomicInteger> firstNameCount = new ConcurrentHashMap<>();
	private final Map<String, AtomicInteger> lastNameCount = new ConcurrentHashMap<>();
	private final AtomicInteger recordCount = new AtomicInteger(0);
	private final ReentrantLock lock = new ReentrantLock();
	
	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}
	
	public void countRecord(Identity identity) {
		recordCount.incrementAndGet();
	}
	
	@Override
	public void countFirstName(Identity identity) {
		String firstName = identity.getName().split(" ")[0];
		
		synchronized ( firstNameCount ) {
			if ( !firstNameCount.containsKey(firstName) ) {
				firstNameCount.put(firstName, new AtomicInteger(1));
			} else {
				AtomicInteger count = firstNameCount.get(firstName);
				count.incrementAndGet();
				firstNameCount.put(firstName, count);
			}
		}
	}
	
	@Override
	public void countLastName(Identity identity) {
		String lastName = identity.getName().split(" ")[1];
		
		synchronized ( lastNameCount ) {
			if ( !lastNameCount.containsKey(lastName) ) {
				lastNameCount.put(lastName, new AtomicInteger(1));
			} else {
				AtomicInteger count = lastNameCount.get(lastName);
				count.incrementAndGet();
				lastNameCount.put(lastName, count);
			}
		}
	}
	
	@Override
	public void countAge(Identity identity) {
		Integer age = identity.getAge();
		
		synchronized ( ageCount ) {
			if ( !ageCount.containsKey(age) ) {
				ageCount.put(age, new AtomicInteger(1));
			} else {
				AtomicInteger count = ageCount.get(age);
				count.incrementAndGet();
				ageCount.put(age, count);
			}
		}
	}
	
	@Override
	public Integer getRecordCount() {
		return recordCount.get();
	}
}
