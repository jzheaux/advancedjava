package com.joshcummings.codeplay.concurrency.splitting;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;
import com.joshcummings.codeplay.concurrency.single.MultiStrategyIdentityReader;

public class BroadcastingMultiStrategyIdentityReader extends
		MultiStrategyIdentityReader {
	
	private ExecutorService pool = Executors.newCachedThreadPool();
	
	public BroadcastingMultiStrategyIdentityReader(
			List<IdentityReader> readers, MalformedBatchRepository repository) {
		super(readers, repository);
	}

	@Override
	public Identity read(InputStream is) {
		try ( CopyingInputStream cis = new CopyingInputStream(is); ) {
			try {
				return primary.read(cis);
			} catch ( Exception e ) {
				List<Callable<Identity>> tasks = new ArrayList<>();
				
				for ( IdentityReader reader : readers ) {
					tasks.add(new Callable<Identity>() {
						@Override
						public Identity call() {
							try {
								return reader.read(cis.reread());
							} catch ( Exception e ) {
								return new BadIdentity();
							}
						}
					});
				}

				try {
					List<Future<Identity>> futures = pool.invokeAll(tasks);
					
					for ( Future<Identity> future : futures ) {
						try {
							Identity candidate = future.get();
							if ( !( candidate instanceof BadIdentity ) ) {
								return candidate;
							}
						} catch ( Exception f ) {
							// try another one
						}
					}
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
				}
				
				repository.addIdentity(cis.reread(), "Tried all identity serialization strategies and all failed");
			}
		} catch ( IOException e ) {
			throw new IllegalStateException("Something terrible happened with the re-read stream", e);
		}
	
		return read(is);
	}

	
}
