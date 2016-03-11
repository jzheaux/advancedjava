package com.joshcummings.codeplay.concurrency.dependency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.joshcummings.codeplay.concurrency.AddressVerifier;
import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.EmailFormatter;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.IdentityService;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;
import com.joshcummings.codeplay.concurrency.NoValidAddressesException;
import com.joshcummings.codeplay.concurrency.PhoneNumberFormatter;
import com.joshcummings.codeplay.concurrency.StatsLedger;
import com.joshcummings.codeplay.concurrency.StatsLedger.StatsEntry;



public class CompletableFutureIdentityPipeline {
	private MalformedBatchRepository malformed; // fire and forget
	private IdentityReader identityReader; 
	private AddressVerifier addressVerifier;
	private PhoneNumberFormatter phoneNumberFormatter;
	private EmailFormatter emailFormatter;
	private IdentityService identityService;
	private StatsLedger statsLedger;
	
	private ExecutorService es = Executors.newCachedThreadPool();
	
	public CompletableFutureIdentityPipeline(MalformedBatchRepository malformed, IdentityReader identityReader, AddressVerifier addressVerifier,
			PhoneNumberFormatter phoneNumberFormatter, EmailFormatter emailFormatter, IdentityService identityService, StatsLedger statsLedger) {
		this.malformed = malformed;
		this.identityReader = identityReader;
		this.addressVerifier = addressVerifier;
		this.phoneNumberFormatter = phoneNumberFormatter;
		this.emailFormatter = emailFormatter;
		this.identityService = identityService;
		this.statsLedger = statsLedger;
	}

	public void process(InputStream input) {
		CompletableFuture.supplyAsync(() -> readIdentity(input))
			.thenAccept(
				(identity) -> {
 					if ( identity != null ) {
						if ( !(identity instanceof BadIdentity) ) {
	 						es.submit(() ->
								{
									try {	
										CompletableFuture<Void> addresses = CompletableFuture.runAsync(() -> validateAddresses(identity));
										CompletableFuture<Void> phone = CompletableFuture.runAsync(() -> phoneNumberFormatter.format(identity));
										CompletableFuture<Void> emailAddress = CompletableFuture.runAsync(() -> emailFormatter.format(identity));
										
										
										// Note that regardless, allOf returns a CompletableFuture<Void> meaning
										// that the results of the individual CFs will not be passed through to
										// any of the continuations. You can, of course, simply retreive those
										// results by referencing them directly
										
										CompletableFuture.allOf(addresses, phone, emailAddress)
											.thenRunAsync(() -> {
												// If we call addresses.get(), we can get the result, and it will not block since
												// the semantics of allOf are that addresses, phone, and emailAddress have all completed
												if ( !identityService.persistOrUpdateBestMatch(identity) ) {
													statsLedger.recordEntry(new StatsEntry(identity));
												}
											}).exceptionally((e) -> {
												malformed.addIdentity(identity, e.getMessage());
												return null;
											}).get();
			
									} catch ( ExecutionException e ) {
										malformed.addIdentity(identity, e.getMessage());
									} catch ( InterruptedException e ) {
										Thread.currentThread().interrupt();
									}
								});
						}
						process(input);
					}
				});
	}
	
	public void processOld(InputStream input) {
		Identity i;
		while ( ( i = readIdentity(input) ) != null ){
			final Identity identity = i;
			es.submit( () -> {
				try {
					
					CompletableFuture<Void> addresses = CompletableFuture.runAsync(() -> validateAddresses(identity));
					CompletableFuture<Void> phone = CompletableFuture.runAsync(() -> phoneNumberFormatter.format(identity));
					CompletableFuture<Void> emailAddress = CompletableFuture.runAsync(() -> emailFormatter.format(identity));
					
					CompletableFuture.allOf(addresses, phone, emailAddress)
						.thenRunAsync(() -> {
							identityService.persistOrUpdateBestMatch(identity);
							statsLedger.recordEntry(new StatsEntry(identity));
						}).exceptionally((e) -> {
							malformed.addIdentity(identity, e.getMessage());
							return null;
						}).get();

				} catch ( ExecutionException e ) {
					malformed.addIdentity(identity, e.getMessage());
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
				}
			});
		}
	}
	
	private void validateAddresses(Identity identity) {
		addressVerifier.verify(identity.getAddresses());
		
		if ( identity.getAddresses().stream().allMatch(a -> !a.isVerified())) {
			throw new NoValidAddressesException();
		}
	}

	private Identity readIdentity(InputStream is) {
		return identityReader.read(is);
	}
}
