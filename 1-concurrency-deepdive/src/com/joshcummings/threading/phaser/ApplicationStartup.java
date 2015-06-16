package com.joshcummings.threading.phaser;

import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationStartup {
	private static SecureRandom random = new SecureRandom();
	
	private CountDownLatch latch = new CountDownLatch(3);
	private ExecutorService es = Executors.newCachedThreadPool();
	
	public ApplicationStartup() {
		System.out.printf("Application starting...%n");
		es.submit(() -> new Service("AuthenticationService", latch));
		es.submit(() -> new Service("ConfigurationService", latch));
		es.submit(() -> new Service("CatService", latch));
		try {
			latch.await();
		} catch (InterruptedException e) {
			// bad
		}
		System.out.printf("Application started.%n");
	}

	private static class Service {		
		public Service(String name, CountDownLatch latch) {
			System.out.printf("%s starting up...%n", name);
			try {
				Thread.sleep(random.nextInt(4));
			} catch (InterruptedException e) {
				// bad
			} finally {
				latch.countDown();
			}
			System.out.printf("%s started.%n", name);
		}
	}
	
	public static void main(String[] args) {
		new ApplicationStartup();
	}
}
