package com.joshcummings.atomicity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class LostUpdate {
	private volatile int count;
	
	public void increment() {
		synchronized ( this ) {
			for ( int i = 0; i < 1000000; i++ ) {
				count++;
			}
		}
	}

	public void finished() {
		System.out.printf("The final count is: %d%n", count);
	}
	
	/**
	 * Incrementation is NOT atomic in Java. This means that two threads accessing the same
	 * variable may interleave their commands in such a way as to loose a value in the process.
	 * 
	 * Fix this by making "increment" synchronized. What other ways are there to alter it
	 * for similar behavior?
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)  throws Exception {
		ExecutorService es = Executors.newFixedThreadPool(2);
		LostUpdate lu = new LostUpdate();
		Future<?> f1 = es.submit(lu::increment);
		Future<?> f2 = es.submit(lu::increment);
		f1.get(); f2.get();
		lu.finished();
		es.shutdown();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
