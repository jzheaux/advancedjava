package com.joshcummings.codeplay.concurrency.throttle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.joshcummings.codeplay.concurrency.Address;
import com.joshcummings.codeplay.concurrency.AddressVerifier;

public class BatchingAddressVerifier implements AddressVerifier {
	private Queue<Address> toBeVerified = new ConcurrentLinkedQueue<>();
	
	private final AddressVerifier delegate;
	
	private final CyclicBarrier batcher;

	private final int batchSize;
	private final int perVerifyWaitTime;
	
	private final ExecutorService es = Executors.newCachedThreadPool();
	
	public BatchingAddressVerifier(AddressVerifier delegate, int batchSize, int perVerifyWaitTime) {
		this.delegate = delegate;
		this.batchSize = batchSize;
		this.perVerifyWaitTime = perVerifyWaitTime;
		batcher = new CyclicBarrier(batchSize);
	}
	
	private List<Address> pollTen() {
		List<Address> batch = new ArrayList<>(batchSize);
		int count = 0;
		while ( count < batchSize ) {
			batch.add(toBeVerified.poll());
			count++;
		}
		return batch;
	}
	
	private void sendBatch() {
		delegate.verify(pollTen());
	}
	
	@Override
	public void verify(List<Address> addresses) {
		Queue<Address> overflow = new ConcurrentLinkedQueue<>(addresses);
		CountDownLatch cdl = new CountDownLatch(addresses.size());
		
		addresses.stream().map(address ->
			new Address(address.getAddress1(), address.getCity(), address.getState(), address.getZipCode()) {
				public void setVerified(boolean verified) {
					address.setVerified(verified);
					overflow.remove(address);
					cdl.countDown();
				}
			})
			.forEach(address -> {
				toBeVerified.add(address);
				es.submit(() -> {
					try {
						if ( batcher.await(perVerifyWaitTime, TimeUnit.MILLISECONDS) == 0 ) {
							sendBatch();
						}
					}
					catch (TimeoutException | BrokenBarrierException | InterruptedException e) {
						cdl.countDown();
					}
				});
			
			});
		
		try {
			cdl.await(perVerifyWaitTime*overflow.size(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if ( !overflow.isEmpty() ) {
			System.out.println("Processing an overflow of size: " + overflow.size());
			delegate.verify(new ArrayList<>(overflow));
		}
	}
	
	public static Address mockAddress(int id) {
		return new Address(id + ": asdf", "asdf", "asdf", "asdf");
	}
	
/*	public static void main(String[] args) {
		int numberOfJobs = 9997;
		int batchSize = 10;
		AtomicInteger timesInvoked = new AtomicInteger(0);
		AtomicInteger addressVerified = new AtomicInteger(0);
		
		CountDownLatch cdl = new CountDownLatch(numberOfJobs);
		AddressVerifier av = new AddressVerifier() {
			@Override
			public void verify(List<Address> addresses) {
				timesInvoked.incrementAndGet();
				try { Thread.sleep(250); } catch ( InterruptedException e ) {  just move on  }
				addresses.forEach((address) -> {
					System.out.println("Verifying address #" + address.getAddress1());
					address.setVerified(true);
					cdl.countDown();
					addressVerified.incrementAndGet();
				});
			}
		};
		
		BatchingAddressVerifier pav = new BatchingAddressVerifier(av, batchSize, 500);
		
		Queue<Long> times = new ConcurrentLinkedQueue<>();
		
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
				pav.verify(addresses);
				times.add(System.nanoTime() - time);
			}).start();
		}
		
		System.out.println("Total: " + total);
		
		try {
			cdl.await();
		} catch ( InterruptedException e ) {
			System.out.println("Couldn't wait");
		}
		
		System.out.println(times);
		double average = times.stream().mapToLong((time) -> time.longValue()).average().getAsDouble() / 1000000;
		System.out.println(average);
		System.out.println(timesInvoked);
		System.out.println(average * timesInvoked.get());
		
		pav.es.shutdown();
		
		System.out.println("Total verified: " + addressVerified.get());
	}*/
}