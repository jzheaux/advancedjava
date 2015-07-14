package com.joshcummings.badpublishing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BadPublishing {
	public Holder holder;
	
	public void initialize() {
		holder = new Holder(42);
	}
	
	/**
	 * As crazy as it sounds, it is possible to have holder be in a half-constructed
	 * state when the second thread begins!
	 * 
	 * Safely publish the holder by doing one of four things:
	 * 
	 * Initialize the object reference from a static initializer;
	 * Storing the reference to it into a volatile field or AtomicReference;
	 * Storing the reference to it into a final field of a properly constructed object; or
     * Storing a reference to it into a field that is properly guarded by a lock.
     * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ExecutorService es = Executors.newFixedThreadPool(100);
		for ( int i = 0; i < 10000; i++ ) {
			BadPublishing bp = new BadPublishing();
			
			CountDownLatch cdl = new CountDownLatch(101);
			
			Callable<Object> publisher = () -> {
				bp.initialize(); 
				cdl.countDown();
				return null;
			};
			
			Callable<Object> accessor = () -> { 
				if ( bp.holder != null ) { 
					bp.holder.assertSanity();
				}
				cdl.countDown();
				return null;
			};
			
			Collection<Callable<Object>> tasks = new ArrayList<>();
			tasks.add(publisher);
			for ( int j = 0; j < 100; j++ ) {
				tasks.add(accessor);
			}
			es.invokeAll(tasks);
			cdl.await();
		}
		System.out.println("Done!");
	}
}
