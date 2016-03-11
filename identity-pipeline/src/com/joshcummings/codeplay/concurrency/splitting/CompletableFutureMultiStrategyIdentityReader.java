package com.joshcummings.codeplay.concurrency.splitting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.CompletablePatterns;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;
import com.joshcummings.codeplay.concurrency.single.MultiStrategyIdentityReader;

public class CompletableFutureMultiStrategyIdentityReader extends
		MultiStrategyIdentityReader {
	
	public CompletableFutureMultiStrategyIdentityReader(List<IdentityReader> readers,
			MalformedBatchRepository repository) {
		super(readers, repository);
	}

	private ExecutorService pool = Executors.newWorkStealingPool();
	
	private Identity readIdentity(CopyingInputStream cis, IdentityReader ir, Function<? super String, ? extends RuntimeException> s) {
		Identity i = ir.read(cis);
		if ( i instanceof BadIdentity ) {
			String message = "Identity Reader " + ir + " failed to read identity";
			repository.addIdentity(cis.reread(), message);
			throw s.apply(message);
		}
		return i;
	}
	
	public void readAsync(InputStream is, Consumer<Identity> c) {
		CopyingInputStream cis = new CopyingInputStream(is);
		
		// if we don't provide our own thread pool, then the common thread pool will be used
		CompletableFuture<Identity> primaryProvider = CompletableFuture.supplyAsync(() -> {
			return readIdentity(cis, primary, RuntimeException::new);
		}, pool);
		
		List<CompletableFuture<Identity>> secondaryProviders = new ArrayList<>();
		for ( IdentityReader secondary : readers ) {
			CompletableFuture<Identity> secondaryProvider = CompletableFuture.supplyAsync(() -> {
				return readIdentity(cis, secondary, RuntimeException::new);
			}, pool);
			secondaryProviders.add(secondaryProvider);
		}
		
		primaryProvider
			.thenAccept(id -> c.accept((Identity)c))
			.exceptionally(exception -> {
				
				// The semantics of "anyOf" are that it will short-circuit whether any
				// task completes normally OR exceptionally
				CompletablePatterns.tryAnyOf(secondaryProviders.toArray(new CompletableFuture[secondaryProviders.size()]))
					.thenAccept(id -> c.accept((Identity)c))
					.exceptionally(secondFailure -> {
						c.accept(new BadIdentity());
						return null;
					});

				return null;
			});
	}
}
