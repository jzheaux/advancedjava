package com.joshcummings.lazysingleton;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LazySingleton {
	private /*volatile*/ static LazySingleton ls;
	
	public static LazySingleton getInstance() {
		//if ( ls == null ) {
			//synchronized ( LazySingleton.class ) {
				if ( ls == null ) {
					ls = new LazySingleton();
				}
			//}
		//}
		return ls;
	}
	
	/**
	 * If you run the test below as-is, without changing anything above or below,
	 * you will see that there are times when two versions of LazySingleton are 
	 * instantiated!
	 * 
	 * Explore the combinations of thread safety checks that you can introduce
	 * to get the corruption down to zero.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(100);
		int numberOfCorruptions = 0;
		int numberOfTests = 100000;
		for ( int j = 0; j < numberOfTests; j++ ) {
			Set<LazySingleton> values = new HashSet<>(); //Collections.newSetFromMap(new ConcurrentHashMap<>());
			CountDownLatch cdl = new CountDownLatch(10);
			for ( int i = 0; i < 10; i++ ) {
				es.submit(() -> {
					values.add(LazySingleton.getInstance());
					cdl.countDown();
				});
			}
			cdl.await();
			if ( values.size() > 1 ) {
				System.out.println("Duplication on iteration " + j);
				numberOfCorruptions++;
			}
			ls = null;
		}
		System.out.println("Done! " + ( numberOfCorruptions / (double)numberOfTests ) * 100 + "% corrupted");
	}
}
