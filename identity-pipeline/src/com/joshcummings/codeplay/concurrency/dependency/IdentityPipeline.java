package com.joshcummings.codeplay.concurrency.dependency;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
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
import com.joshcummings.codeplay.concurrency.splitting.AsyncMultiStrategyIdentityReader;




public class IdentityPipeline {
	private MalformedBatchRepository malformed; // fire and forget
	private IdentityReader identityReader; 
	private AddressVerifier addressVerifier;
	private PhoneNumberFormatter phoneNumberFormatter;
	private EmailFormatter emailFormatter;
	private IdentityService identityService;
	private StatsLedger statsLedger;
	
	private ExecutorService es = Executors.newWorkStealingPool();
	
	public IdentityPipeline(MalformedBatchRepository malformed, IdentityReader identityReader, AddressVerifier addressVerifier,
			PhoneNumberFormatter phoneNumberFormatter, EmailFormatter emailFormatter, IdentityService identityService, StatsLedger statsLedger) {
		this.malformed = malformed;
		this.identityReader = identityReader;
		this.addressVerifier = addressVerifier;
		this.phoneNumberFormatter = phoneNumberFormatter;
		this.emailFormatter = emailFormatter;
		this.identityService = identityService;
		this.statsLedger = statsLedger;
	}
	
	public void processAsync(InputStream input) {
		AsyncMultiStrategyIdentityReader reader = (AsyncMultiStrategyIdentityReader)identityReader;
		reader.readAsync(input, identity -> {
			if ( identity instanceof BadIdentity ) {
				processAsync(input);
			} else if ( identity != null ) {
				es.submit(() -> {
					System.out.println("Processing identity #" + identity.getId());
					// verify address
					try {
						// slow process for each address, publish requests here, address verifier self-throttles;
						// callback to say when an address has been verified
						CountDownLatch cdl = new CountDownLatch(3);
						
						es.submit(() -> {
							System.out.println("Validating addresses for #" + identity.getId());
							validateAddresses(identity);
							cdl.countDown();
						});
						
						// format phone number and email address
						es.submit(() -> {
							System.out.println("Validating phone number for #" + identity.getId());
							phoneNumberFormatter.format(identity);
							cdl.countDown();
						});
						
						es.submit(() -> {
							System.out.println("Validating email address for #" + identity.getId());
							emailFormatter.format(identity);
							cdl.countDown();
						});
		
						// dependent on everything succeeding
						// client-side load? distributed-then-aggregated effort
						//es.submit(() -> {
						try {
							// This is currently waiting on the three jobs above, meaning that we'd
							// like to make this thread schedulable while it waits. For that reason,
							// we use the work-stealing pool to schedule it.
							if ( cdl.await(3000, TimeUnit.MILLISECONDS) ) {
								System.out.println("Persisting identity #" + identity.getId());
								if ( !identityService.persistOrUpdateBestMatch(identity) ) {
								
									// shared resource for which all threads will contend
									System.out.println("Recording identity #" + identity.getId());
									statsLedger.recordEntry(new StatsEntry(identity));
									System.out.println("Completed record #" + identity.getId());
								}
							} else {
								malformed.addIdentity(identity, "Couldn't verify all parts of identity.");
							}
						} catch (InterruptedException e) {
							malformed.addIdentity(identity, e.getMessage());
						}
					} catch ( NoValidAddressesException e ) {
						malformed.addIdentity(identity, e.getMessage());
					}
				});
				processAsync(input);
			}
		});
	}
	
	public void process(InputStream input) {
		Identity i;
		while ( ( i = readIdentity(input) ) != null ){
			final Identity identity = i;
			es.submit( () -> {
				System.out.println("Processing identity #" + identity.getId());
				// verify address
				try {
					// Each verification job can be done independently from one another;
					// however the persisting of the identity IS dependent on all three completing.
					
					CountDownLatch cdl = new CountDownLatch(3);
					
					es.submit(() -> {
						System.out.println("Validating addresses for #" + identity.getId());
						validateAddresses(identity);
						cdl.countDown();
					});
					
					// format phone number and email address
					es.submit(() -> {
						System.out.println("Validating phone number for #" + identity.getId());
						phoneNumberFormatter.format(identity);
						cdl.countDown();
					});
					
					es.submit(() -> {
						System.out.println("Validating email address for #" + identity.getId());
						emailFormatter.format(identity);
						cdl.countDown();
					});
	
					// dependent on everything succeeding
					// client-side load? distributed-then-aggregated effort
					//es.submit(() -> {
					try {
						// This is currently waiting on the three jobs above, meaning that we'd
						// like to make this thread schedulable while it waits. For that reason,
						// we use the work-stealing pool to schedule it.2
						
						if ( cdl.await(3000, TimeUnit.MILLISECONDS) ) {
							System.out.println("Persisting identity #" + identity.getId());
							if ( !identityService.persistOrUpdateBestMatch(identity) ) {
							
								// shared resource for which all threads will contend
								System.out.println("Recording identity #" + identity.getId());
								statsLedger.recordEntry(new StatsEntry(identity));
								System.out.println("Completed record #" + identity.getId());
							}
						} else {
							malformed.addIdentity(identity, "Couldn't verify all parts of identity.");
						}
					} catch (InterruptedException e) {
						malformed.addIdentity(identity, e.getMessage());
					}
				} catch ( NoValidAddressesException e ) {
					malformed.addIdentity(identity, e.getMessage());
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
	
	// try all and proceed with whichever returned first
	// -- hmmm, as slow as the slowest if all are wrong...
	// as fast as the fastest if at least one is right
	// => what is the probability of one being wrong?
	private Identity readIdentity(InputStream is) {
		return identityReader.read(is);
	}
}
