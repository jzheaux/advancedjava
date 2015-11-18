package com.joshcummings.codeplay.concurrency.single;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.StatsCounter;

/**
 * Here, we use ReentrantLock just to demonstrate the additional flexibility we get with the API
 * 
 * We could also have a method like "count all" that syncrhonizes on a common mutex to get the same effect.
 * 
 * @author jzheaux
 *
 */
public class BasicStatsCounter implements StatsCounter {
	private final Map<Integer, Integer> ageMap = new ConcurrentHashMap<>();
	private final Map<String, Integer> firstNameMap = new ConcurrentHashMap<>();
	private final Map<String, Integer> lastNameMap = new ConcurrentHashMap<>();
	private Integer recordCount = 0;
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public void countRecord(Identity identity) {
		recordCount++;
	}
	
	@Override
	public void countFirstName(Identity identity) {
		String firstName = identity.getName().split(" ")[0];
		
		Integer count = withDefault(firstNameMap.get(firstName), 0);
		firstNameMap.put(firstName, count + 1);
	}
	
	@Override
	public void countLastName(Identity identity) {
		String lastName = identity.getName().split(" ")[1];
		
		Integer count = withDefault(lastNameMap.get(lastName), 0);
		lastNameMap.put(lastName, count + 1);
	}
	
	@Override
	public void countAge(Identity identity) {
		Integer age = identity.getAge();
		
		Integer count = withDefault(ageMap.get(age), 0);
		ageMap.put(age, count + 1);
	}
	
	@Override
	public void writeStats(OutputStream os) {
		new PrintWriter(os, true).println("Number of records: " + recordCount);
	}
	
	private Integer withDefault(Integer value, Integer backup) {
		if ( value == null ) {
			return value;
		}
		return backup;
	}
}
