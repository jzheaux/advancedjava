package com.joshcummings.codeplay.concurrency.splitting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedIdentityRepository;
import com.joshcummings.codeplay.concurrency.single.CopyingInputStream;
import com.joshcummings.codeplay.concurrency.single.MultiStrategyIdentityReader;

public class CompletableFutureMultiStrategyIdentityReader extends
		MultiStrategyIdentityReader {
	
	public CompletableFutureMultiStrategyIdentityReader(List<IdentityReader> readers,
			MalformedIdentityRepository repository) {
		super(readers, repository);
	}

	private ExecutorService pool = Executors.newWorkStealingPool();
	
	public void readAsync(InputStream is, Consumer<Identity> c) {
		CopyingInputStream cis = new CopyingInputStream(is);
		CompletableFuture<Identity> primaryProvider = CompletableFuture.supplyAsync(() -> {
			Identity i;
			try {
				i = primary.read(cis);
			} catch ( Exception e ) {
				i = new BadIdentity();
			}
			if ( i == null || i instanceof BadIdentity ) {
				throw new RuntimeException();
			} else {
				return i;
			}
		});
		
		List<CompletableFuture<Identity>> secondaryProviders = new ArrayList<>();
		for ( IdentityReader secondary : readers ) {
			CompletableFuture<Identity> secondaryProvider = CompletableFuture.supplyAsync(() -> {
				Identity i;
				try {
					i = secondary.read(cis.reread());
				} catch ( Exception e ) {
					i = new BadIdentity();
				}
				if ( i == null || i instanceof BadIdentity ) {
					throw new RuntimeException();
				} else { 
					return i;
				}
			});
			secondaryProviders.add(secondaryProvider);
		}
		
		CompletableFuture.anyOf(primaryProvider)
			.thenAccept(id -> c.accept((Identity)c))
			.exceptionally(exception -> {
				CompletableFuture.anyOf(secondaryProviders.toArray(new CompletableFuture[secondaryProviders.size()]))
					.thenAccept(id -> c.accept((Identity)c));
				return null;
			});
	}
}
