package com.joshcummings.codeplay.concurrency.fireandforget;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;

public class ProducerConsumerMalformedBatchRepository implements
		MalformedBatchRepository {
	private MalformedBatchRepository delegate;
	
	private BlockingQueue<Entry<Identity>> invalidIdentities = new LinkedBlockingQueue<>();
	private BlockingQueue<Entry<InputStream>> malformedIdentities = new LinkedBlockingQueue<>();
	
	private ForkJoinPool consumers = new ForkJoinPool(2);
	
	public ProducerConsumerMalformedBatchRepository(MalformedBatchRepository delegate) {
		this.delegate = delegate;
		
		consumers.execute(this::processInvalidIdentities);
		consumers.execute(this::processMalformedIdentities);
	}
	
	@Override
	public void addIdentity(Identity identity, String reason) {
		System.out.println("Reporting identity #" + identity.getId());
		invalidIdentities.add(new Entry<Identity>(identity, reason));
	}

	@Override
	public void addIdentity(InputStream message, String reason) {
		malformedIdentities.add(new Entry<InputStream>(message, reason));
	}
	
	private void processInvalidIdentities() {
		Entry<Identity> e = takeOrAlert(invalidIdentities);
		while ( e != null ) {
			delegate.addIdentity(e.value, e.reason);
			e = takeOrAlert(invalidIdentities);
		}
	}
	
	private void processMalformedIdentities() {
		Entry<InputStream> e = takeOrAlert(malformedIdentities);
		while ( e != null ) {
			delegate.addIdentity(e.value, e.reason);
			e = takeOrAlert(malformedIdentities);
		}
	}
	
	private <T> T takeOrAlert(BlockingQueue<T> q) {
		try {
			return q.take();
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
	
	private class Entry<T> {
		public final T value;
		public final String reason;
		
		public Entry(T value, String reason) {
			this.value = value;
			this.reason = reason;
		}
	}

}
