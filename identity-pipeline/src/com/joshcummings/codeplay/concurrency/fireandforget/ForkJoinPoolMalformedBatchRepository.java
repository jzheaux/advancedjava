package com.joshcummings.codeplay.concurrency.fireandforget;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;

public class ForkJoinPoolMalformedBatchRepository implements
		MalformedBatchRepository {
	private ExecutorService pool = Executors.newWorkStealingPool();
	
	private MalformedBatchRepository delegate;
	
	public ForkJoinPoolMalformedBatchRepository(MalformedBatchRepository delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void addIdentity(Identity identity, String reason) {
		pool.submit(() -> delegate.addIdentity(identity, reason));
	}

	@Override
	public void addIdentity(InputStream message, String reason) {
		pool.submit(() -> delegate.addIdentity(message, reason));
	}
}
