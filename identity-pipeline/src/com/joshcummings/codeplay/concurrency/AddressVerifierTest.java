package com.joshcummings.codeplay.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class AddressVerifierTest {

	private static Address mockAddress(int id) {
		return new Address(id + ": asdf", "asdf", "asdf", "asdf");
	}
	
	@Test
	public void testWithBatcher() {
		int numberOfJobs = 9997;
		int batchSize = 10;
		AtomicInteger timesInvoked = new AtomicInteger(0);
		AtomicInteger addressVerified = new AtomicInteger(0);
		
		CountDownLatch cdl = new CountDownLatch(numberOfJobs);
		AddressVerifier av = new AddressVerifier() {
			@Override
			public void verify(List<Address> addresses) {
				timesInvoked.incrementAndGet();
				try { Thread.sleep(250); } catch ( InterruptedException e ) { /* just move on */ }
				addresses.forEach((address) -> {
					System.out.println("Verifying address #" + address.getAddress1());
					address.setVerified(true);
					cdl.countDown();
					addressVerified.incrementAndGet();
				});
			}
		};
		
		Batcher<Address> batcher = new Batcher<>(batchSize, 1000);
		
		Queue<Long> times = new ConcurrentLinkedQueue<>();
		
		/* Randomly segment numberOfJobs into increments sized from 1 to 5 */
		Random rand = new Random();
		int total = 0;
		while ( total < numberOfJobs ) {
			int maxHowMany = Math.min(5, numberOfJobs - total);
			int howMany = rand.nextInt(maxHowMany) + 1;
			List<Address> addresses = new ArrayList<>(howMany);
			for ( int j = 0; j < howMany; j++ ) {
				addresses.add(mockAddress(total));
				total++;
			}
			
			new Thread(() -> {
				long time = System.nanoTime();
				batcher.batch(addresses, av::verify);
				times.add(System.nanoTime() - time);
			}).start();
		}
		
		/* Wait for all the addresses to be verified */
		try {
			cdl.await();
		} catch ( InterruptedException e ) {
			System.out.println("Couldn't wait");
		}
		
		batcher.close();
		
		Assert.assertEquals(total, addressVerified.get());
		Assert.assertTrue(total / 10 + total % 10 >= timesInvoked.get());
	}

}
