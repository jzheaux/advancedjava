package com.joshcummings.codeplay.concurrency.throttle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.joshcummings.codeplay.concurrency.Address;
import com.joshcummings.codeplay.concurrency.AddressVerifier;

public class PhasingAddressVerifier implements AddressVerifier {
	private BlockingQueue<PhasedOperation> toBeVerified = new LinkedBlockingQueue<>();
	
	private final AddressVerifier delegate;
	
	private final Phaser batcher;
	private int batchSize;
	
	private final ExecutorService fetcher = Executors.newFixedThreadPool(1);
	private final ExecutorService runner = Executors.newCachedThreadPool();
	
	public PhasingAddressVerifier(AddressVerifier delegate, int batchSize) {
		this.delegate = delegate;
		this.batchSize = batchSize;
		batcher = new Phaser(batchSize) {
			@Override
			protected boolean onAdvance(int phase, int registeredParties) {
				return false;
			}
		};
		
		fetcher.submit(() -> {
			int phase = 0;
			while ( !Thread.currentThread().isInterrupted() ) {
				try {
					batcher.awaitAdvanceInterruptibly(phase, 50, TimeUnit.MILLISECONDS);
					sendBatch();
					phase++;
				} catch ( TimeoutException e ) {
					System.out.println("Timed out while waiting on batch #" + phase + ". Will send what we have.");
					sendBatch();
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}
	
	private void sendBatch(Collection<PhasedOperation> batch) {
		runner.submit(() -> {
			//System.out.println("Processing batch of size " + batch.size());
			List<Address> addresses = batch.stream().map(p -> p.a).collect(Collectors.toList());
			delegate.verify(addresses);
			batch.stream().forEach(p -> p.p.arrive());
		});
	}
	
	private List<PhasedOperation> pollBatch() {
		List<PhasedOperation> batch = new ArrayList<>(batchSize);
		/*int count = 0;
		while ( count < batchSize ) {
			PhasedOperation po = toBeVerified.poll();
			if ( po == null ) {
				break;
			}
			batch.add(po);
			count++;
		}*/
		toBeVerified.drainTo(batch, batchSize);
		return batch;
	}

	private void sendBatch() {
		sendBatch(pollBatch());
	}
	
	public void setBatchSize(int batchSize) {
		if ( batchSize > this.batchSize ) {
			this.batcher.bulkRegister(batchSize - this.batchSize);
		} else {
			for ( int i = 0; i < this.batchSize - batchSize; i++ ) {
				this.batcher.arriveAndDeregister();
			}
		}
		this.batchSize = batchSize;
	}
	
	private static class PhasedOperation {
		public final Address a;
		public final Phaser p;
		
		public PhasedOperation(Address a, Phaser p) {
			this.a = a;
			this.p = p;
		}
	}

	@Override
	public void verify(List<Address> addresses) {
		this.verify(addresses, addresses.size());
	}
	
	public void verify(List<Address> addresses, int size) {
		Phaser p = new Phaser(addresses.size());

		addresses.forEach(address -> {
			try {
				toBeVerified.offer(new PhasedOperation(address, p));			
				batcher.arrive();
			} catch ( IllegalStateException e ) {
				// log this
			}
		});
		
		p.awaitAdvance(0);
	}
	
	public static Address mockAddress(int id) {
		return new Address(id + ": asdf", "asdf", "asdf", "asdf");
	}
	
	public static void main(String[] args) {
		int numberOfJobs = 99997;
		int batchSize = 1000;
		AtomicInteger timesInvoked = new AtomicInteger(0);
		AtomicInteger addressVerified = new AtomicInteger(0);
		Queue<Long> times = new ConcurrentLinkedQueue<>();
		Queue<Long> delegateTimes = new ConcurrentLinkedQueue<>();
		
		CountDownLatch cdl = new CountDownLatch(numberOfJobs);
		AddressVerifier av = new AddressVerifier() {
			private int j;
			@Override
			public void verify(List<Address> addresses) {
				long time = System.nanoTime();
				timesInvoked.incrementAndGet();
				for ( int i = 0; i < 100000000; i++ ) {
					j+=i;
				}
				
				addresses.forEach((address) -> {
					address.setVerified(true);
					cdl.countDown();
					addressVerified.incrementAndGet();
				});
				delegateTimes.add(System.nanoTime() - time);
			}
		};
		
		PhasingAddressVerifier pav = new PhasingAddressVerifier(av, batchSize);
		

		
		Random rand = new Random(676325345568L);
		int total = 0;
		AtomicInteger COUNTER = new AtomicInteger();
		ExecutorService sender = Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				ThreadFactory tf = Executors.defaultThreadFactory();
				Thread t = tf.newThread(r);
				t.setName("Sender-" + COUNTER.incrementAndGet());
				return t;
			}
		});
		int numberOfInvocations = 0;
		while ( total < numberOfJobs ) {
			int maxHowMany = Math.min(5, numberOfJobs - total);
			int howMany = rand.nextInt(maxHowMany) + 1;
			List<Address> addresses = new ArrayList<>(howMany);
			for ( int j = 0; j < howMany; j++ ) {
				addresses.add(mockAddress(total));
			}
			total += howMany;
			numberOfInvocations++;
			/*try {
				Thread.sleep(rand.nextInt(200));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			/*if ( total * 2 > numberOfJobs ) {
				pav.setBatchSize(1000);
			}*/

			sender.submit(() -> {
				long time = System.nanoTime();
				pav.verify(addresses, addresses.size());
				times.add(System.nanoTime() - time);
			});
		}
		
		System.out.println("Total: " + total);
		
		try {
			cdl.await();
		} catch ( InterruptedException e ) {
			System.out.println("Couldn't wait");
		}
		
		System.out.println(times);
		double average = times.stream().mapToLong((time) -> time.longValue()).average().getAsDouble() / 1000000;
		double delegateAverage = delegateTimes.stream().mapToLong((time) -> time.longValue()).average().getAsDouble() / 1000000;
		System.out.println(average);
		System.out.println(delegateAverage);
		System.out.println(timesInvoked);
		System.out.println(Math.round(average * timesInvoked.get()));
		System.out.println(Math.round(delegateAverage * numberOfInvocations));
		System.out.println("Speed up: " + (delegateAverage * numberOfInvocations) / (average * timesInvoked.get()) );
		
		pav.fetcher.shutdownNow();
		pav.runner.shutdownNow();
		sender.shutdownNow();
		
		System.out.println("Total verified: " + addressVerified.get());
	}
}