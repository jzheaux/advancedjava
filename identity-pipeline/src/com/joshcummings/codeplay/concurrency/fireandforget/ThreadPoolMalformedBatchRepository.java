package com.joshcummings.codeplay.concurrency.fireandforget;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;

public class ThreadPoolMalformedBatchRepository implements
		MalformedBatchRepository {
	// This means that threads will be provisioned lazily and then decomissioned after 60 seconds of idleness
	private ExecutorService pool = Executors.newCachedThreadPool();
	
	// We'll follow the decorator pattern as much as possible throughout the demo to more easily isolate
	// the concurrency pattern from the surrounding context
	private MalformedBatchRepository delegate;
	
	public ThreadPoolMalformedBatchRepository(MalformedBatchRepository delegate) {
		this.delegate = delegate;
	}
	
	// Fire and Forget! Note that the submit will return immediately regardless of
	// any threads being available in the moment to execute it
	@Override
	public void addIdentity(Identity identity, String reason) {
		// new Thread(() -> delegate.addIdentity(identity, reason)).start();
		pool.submit(() -> delegate.addIdentity(identity, reason));
	}

	// Also notice that this method returns void since, in this case, we have
	// chosen to ignore any results that might be available from the execution
	// of this thread
	@Override
	public void addIdentity(InputStream message, String reason) {
		pool.submit(() -> delegate.addIdentity(message, reason));
	}

}
