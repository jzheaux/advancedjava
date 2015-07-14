package com.joshcummings.forkjoin;

import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Sending a single task out for multi-processing is cumbersome with plain threads.
 * What we want is to say "send this task out to three different search engines and
 * whichever comes back first, take those results and forget about the rest"
 * 
 * @author jzheaux
 *
 */
public class MultiSearchEngine {
	private String results;
	
	public String parallelSearch(String query) {
		Optional<String> results = Stream.of("Google", "Ask", "Yahoo")
			.parallel()
			.map((searchEngineName) ->
				searchEngineName + " got the results first.")
			.findAny();
		
		return results.get();
	}
	
	public String search(String query) throws InterruptedException {
		Consumer<String> searchResultsConsumer = new Consumer<String>() {
			@Override
			public void accept(String t) {
				synchronized ( MultiSearchEngine.class ) {
					if ( results == null ) {
						results = t;
						System.out.println("Recieved results: " + t);
					}
				}
			}
			
		};
		
		Random waitingPeriod = new Random();
		
		Thread google = new Thread(() -> {
			System.out.println("Searching google...");
			try {
				Thread.sleep(waitingPeriod.nextInt(3000));
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
			searchResultsConsumer.accept("Google found the results first.");
		});
		
		Thread ask = new Thread(() -> {
			System.out.println("Searching ask...");
			try {
				Thread.sleep(waitingPeriod.nextInt(3000));
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
			searchResultsConsumer.accept("Ask found the results first.");
		});
		
		Thread yahoo = new Thread(() -> {
			System.out.println("Searching yahoo...");
			try {
				Thread.sleep(waitingPeriod.nextInt(3000));
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
			searchResultsConsumer.accept("Yahoo found the results first.");
		});
		
		google.start();
		ask.start();
		yahoo.start();
		
		google.join();
		ask.join();
		yahoo.join();
		
		return results;
	}
	
	public static void main(String[] args) throws InterruptedException {
		MultiSearchEngine mse = new MultiSearchEngine();
		String results = mse.parallelSearch("carrot-flavored pidgeons");
		System.out.println(results);
	}
	
	
	
	
	
	
	
	
	
}
