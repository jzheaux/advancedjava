package com.joshcummings.codeplay.concurrency.splitting;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;
import com.joshcummings.codeplay.concurrency.single.MultiStrategyIdentityReader;

public class AsyncMultiStrategyIdentityReader extends
		MultiStrategyIdentityReader {
	
	public AsyncMultiStrategyIdentityReader(List<IdentityReader> readers,
			MalformedBatchRepository repository) {
		super(readers, repository);
	}

	private ExecutorService pool = Executors.newWorkStealingPool();
	
	// The basic callback pattern exposes a callback as a parameter and typically
	// precipitates heavier nesting (sometimes called "Callback Hell")
	// in order to maintain appropriate closure context
	public void readAsync(InputStream is, Consumer<Identity> c) {
		CopyingInputStream cis = new CopyingInputStream(is);
		readAsyncPrimary(cis, (future) -> {
			pool.submit(() -> {
				Identity i;
				try {
					i = future.get();
				} catch ( Exception e ) {
					// add something here
					i = new BadIdentity();
				}
				
				if ( i != null && !(i instanceof BadIdentity) ) {
					c.accept(i);
				} else {
					readAsyncSecondaries(cis, (ecs) -> {
						pool.submit(() -> {
							Identity j;
							for ( IdentityReader reader : readers ) {
								try {
									j = ecs.take().get();
								} catch ( Exception e ) {
									j = new BadIdentity();
								}
								if ( j != null && !(j instanceof BadIdentity) ) {
									c.accept(j);
									break;
								}
							}
						});
					});
				}
			});
		});
	}
	
	private void readAsyncPrimary(InputStream is, Consumer<Future<Identity>> c) {
		Future<Identity> identityProvider = pool.submit(() -> {
			try {
				return primary.read(is);
			} catch ( Exception e ) {
				return new BadIdentity();
			}
		});
		
		c.accept(identityProvider);
	}
	
	private void readAsyncSecondaries(CopyingInputStream cis, Consumer<ExecutorCompletionService<Identity>> c) {
		ExecutorCompletionService<Identity> ecs = new ExecutorCompletionService<Identity>(pool);
		for ( IdentityReader reader : readers ) {
			ecs.submit(new Callable<Identity>() {
				@Override
				public Identity call() throws Exception {
					try {
						return reader.read(cis.reread());
					} catch ( Exception e ) {
						return new BadIdentity();
					}
				}
				
			});
		}

		c.accept(ecs);
	}
}
