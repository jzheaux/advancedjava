package com.joshcummings.codeplay.concurrency.fireandforget;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;

public class ProducerConsumerMalformedBatchRepository implements
		MalformedBatchRepository {
	private MalformedBatchRepository delegate;
	
	private BlockingQueue<Entry<Identity>> invalidIdentities = new LinkedBlockingQueue<>();
	private BlockingQueue<Entry<InputStream>> malformedIdentities = new LinkedBlockingQueue<>();
	
	private ExecutorService consumers = Executors.newFixedThreadPool(2,
			new ThreadFactoryBuilder()
				.setNameFormat("Malformed-%d")
				.build());
	
	public ProducerConsumerMalformedBatchRepository(MalformedBatchRepository delegate) {
		this.delegate = delegate;
		
		consumers.execute(this::processInvalidIdentities);
		consumers.execute(this::processMalformedIdentities);
	}
	
	// no threads here, but we are still following the Fire And Forget pattern;
	// once these are added to the queue, this code relies on a separate undefined process
	// to take() work items from the queue
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
			Thread.currentThread().setName("processing-malformed-" + e);
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
		
		public String toString() {
			if ( value instanceof Identity ) {
				return String.valueOf(((Identity)value).getId());
			}
			return "unknown-id";
		}
	}

}
