package com.joshcummings.atomicity;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AtomicDoesntHelpCompoundActions {
	private static Map<String, Vector<String>> concurrentHashMap = new ConcurrentHashMap<>();
	
	/**
	 * ConcurrentHashMap and Vector are both synchronized, so why do we sometimes experience data loss here?
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(100);
		for ( int i = 0; i < 10000; i++ ) {
			CountDownLatch cdl = new CountDownLatch(10);
			for ( int j = 0; j < 10; j++ ) {
				String value = "value" + i;
				es.submit(() -> {
					Vector<String> concurrent = concurrentHashMap.get("values");
					if ( concurrent == null ) {
						concurrent = new Vector<>();
						concurrentHashMap.put("values", concurrent);
					}
					concurrent.add(value);
					cdl.countDown();
				});
			}
			cdl.await();
			if ( concurrentHashMap.get("values").size() != 10 ) {
				System.out.println("Data loss: " + concurrentHashMap.get("values").size());
			}
			concurrentHashMap.remove("values");
		}
	}
}
