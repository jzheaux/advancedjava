package com.joshcummings.async.poor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PoorMansAsync {
	
	private ExecutorService pool = Executors.newCachedThreadPool();
	
	/**
	 * This strategy makes the method non-blocking; it will return immediately,
	 * even though the thread is doing work.
	 * 
	 * @param data
	 * @param c
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void dontCallUsWellCallYou(String data, Consumer<Integer> c) throws InterruptedException, ExecutionException {
		CompletableFuture
			.supplyAsync(() -> {
				try {
					Thread.sleep(3000);
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
				}
				return 24;
			}, pool)
			.thenAccept((i) -> {
				c.accept(i);
			});
	}
	
	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		PoorMansAsync pma = new PoorMansAsync();
		pma.dontCallUsWellCallYou("data",
				(myInteger) -> 
					System.out.println("Received: " + myInteger));
		System.out.println("Done!");
		pma.pool.shutdown();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
