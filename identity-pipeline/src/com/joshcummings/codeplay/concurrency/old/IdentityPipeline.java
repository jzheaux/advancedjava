package com.joshcummings.codeplay.concurrency.old;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.joshcummings.codeplay.concurrency.AddressVerifier;
import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.EmailFormatter;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.IdentityService;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;
import com.joshcummings.codeplay.concurrency.NoValidAddressesException;
import com.joshcummings.codeplay.concurrency.Person;
import com.joshcummings.codeplay.concurrency.PhoneNumberFormatter;
import com.joshcummings.codeplay.concurrency.StatsCounter;

public class IdentityPipeline {
	private MalformedBatchRepository malformed; // fire and forget
	private List<IdentityReader> identityReaders; 
	private AddressVerifier addressVerifier;
	private PhoneNumberFormatter phoneNumberFormatter;
	private EmailFormatter emailFormatter;
	private IdentityService identityService;
	private StatsCounter statsCounter;
	private Set<Identity> verifiedIdentities = new ConcurrentSkipListSet<>();
	// resource contention
	// indexing the data? maybe more regarding the stats counter... lock the stats counter across several invocations for the same record...
	
	// sub tasks... sorting, map reduction... distributed calculation... client-side load balancing?
	
	// compression... encryption... could distribute it... hashing...
	
	// map reduce for performing searches on identity data
	
	// compress incoming identity stream; compressor acts independent of reader
	
	// occasional updates to the output for cyclic barrier
	
	private ExecutorService es = Executors.newCachedThreadPool();
	
	private CyclicBarrier cb = new CyclicBarrier(1500, this::displayStats);
	
	public IdentityPipeline(MalformedBatchRepository malformed, List<IdentityReader> identityReaders, AddressVerifier addressVerifier,
			PhoneNumberFormatter phoneNumberFormatter, EmailFormatter emailFormatter, IdentityService identityService, StatsCounter statsCounter) {
		this.malformed = malformed;
		this.identityReaders = identityReaders;
		this.addressVerifier = addressVerifier;
		this.phoneNumberFormatter = phoneNumberFormatter;
		this.emailFormatter = emailFormatter;
		this.identityService = identityService;
		this.statsCounter = statsCounter;
	}
	
	public void displayStats() {
		System.out.println("Number of records: " + statsCounter.getRecordCount());
	}
	
	public List<Identity> query(Predicate<Identity> pred) {
		List<Identity> filtered = new ArrayList<>();
		for ( Identity i : verifiedIdentities ) {
			if ( pred.test(i) ) {
				filtered.add(i);
			}
		}
		return filtered;
	}
	
	public List<Identity> queryBetter(Predicate<Identity> pred) {
		List<Identity> filtered = new ArrayList<>();
		for ( int i = 0; i < 8; i++ ) {
			int startIndex = i*verifiedIdentities.size() / 8;
			int endIndex = startIndex + verifiedIdentities.size() / 8;
			es.submit(() -> {
				int j = 0;
				for ( Identity id: verifiedIdentities ) {
					if ( j >= startIndex && j < endIndex && pred.test(id) ) {
						filtered.add(id);
					}
					j++;
				}
			});
		}
		return filtered;
	}
	
	public void process(InputStream input) {
		Identity i;
		while ( ( i = readIdentity(input) ) != null ){
			final Identity identity = i;
			es.submit( () -> {
				// verify address
				try {
					// slow process for each address, publish requests here, address verifier self-throttles;
					// callback to say when an address has been verified
					CountDownLatch cdl = new CountDownLatch(3);
					
					es.submit(() -> {
						validateAddresses(identity);
						cdl.countDown();
					});
					
					// format phone number and email address
					es.submit(() -> {
						phoneNumberFormatter.format(identity);
						cdl.countDown();
					});
					
					es.submit(() -> {
						emailFormatter.format(identity);
						cdl.countDown();
					});
	
					// dependent on everything succeeding
					// client-side load? distributed-then-aggregated effort
					//es.submit(() -> {
					try {
						if ( cdl.await(3000, TimeUnit.MILLISECONDS) ) {
							attemptMerge(identity);
							
							// shared resource for which all threads will contend
							statsCounter.countFirstName(identity);
							statsCounter.countLastName(identity);
							statsCounter.countAge(identity);
							statsCounter.countRecord(identity);
						} else {
							malformed.addIdentity(identity, "Couldn't verify all parts of identity.");
						}
					} catch (InterruptedException e) {
						malformed.addIdentity(identity, e.getMessage());
					}
					//});
					
					// reentrant rock: lock-for-update
					
					try {
						cb.await();
					} catch (BrokenBarrierException | InterruptedException e) {
						// we'll run the method anyway.
						displayStats();
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
	
	/**
	 * 
	 * @param identity
	 * @return - whether or not it successfully merged this with another identity
	 */
	private boolean attemptMerge(Identity identity) {
		PriorityQueue<MergeCandidate> candidates = new PriorityQueue<>();
		
		// find candidates
		for (Identity i : verifiedIdentities) {
			int score = 0;
			if ( i.getEmailAddress() != null && i.getEmailAddress().equals(identity.getEmailAddress()) ) {
				score += 50;
			}
			if ( i.getPhoneNumber() != null && i.getPhoneNumber().equals(identity.getPhoneNumber()) ) {
				score += 15;
			}
			if ( i.getName().equals(identity.getName()) ) {
				score += 35;
			}
			if ( score >= 50 ) {
				candidates.offer(new MergeCandidate(i, score));
			}
		}
		
		// pick the best one and lock on it
		for ( MergeCandidate candidate : candidates ) {
			Person id = (Person)candidate.getCandidate();
			if ( id.getLock().tryLock() ) {
				try {
					if ( id.getEmailAddress() == null ) {
						id.setEmailAddress(identity.getEmailAddress());
					}
					if ( id.getPhoneNumber() == null ) {
						id.setPhoneNumber(identity.getPhoneNumber());
					}
					id.addAddresses(identity.getAddresses());
					return true;
				} catch ( Exception e ) {
					// rollback, out of scope
				} finally {
					id.getLock().unlock();
				}
			}
		}
		
		return false;
		
	}
	
	// try all and proceed with whichever returned first
	// -- hmmm, as slow as the slowest if all are wrong...
	// as fast as the fastest if at least one is right
	// => what is the probability of one being wrong?
	private Identity readIdentity(InputStream is) {
		CopyingInputStream cis = new CopyingInputStream(is);
		
		try {
			return identityReaders.get(0).read(cis);
		} catch ( Exception e ) {
			ExecutorCompletionService<Identity> ecs = new ExecutorCompletionService<Identity>(es);
			for ( IdentityReader reader : identityReaders ) {
				ecs.submit(() ->
				{ 
					return reader.read(cis.reread());
				});
			}
			
			try {
				return ecs.take().get();
			} catch ( Exception f ) {
				malformed.addIdentity(cis.reread(), "message");
				return new BadIdentity();
			}
		}
	}
	
	private Identity readIdentitySingle(InputStream is) {
		CopyingInputStream cis = new CopyingInputStream(is);
		for ( IdentityReader reader : identityReaders ) {
			try {
				return reader.read(cis);
			} catch ( Exception e ) {
				// try again...
				cis = new CopyingInputStream(cis.reread());
			}
		}
		
		malformed.addIdentity(cis.reread(), "message");
		return new BadIdentity();
	}
	
	private static class CopyingInputStream extends InputStream {
		private InputStream is;
		private ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		public CopyingInputStream(InputStream is) {
			this.is = is;
		}
		
		@Override
		public int read() throws IOException {
			int i = is.read();
			if ( i != -1 ) {
				baos.write(i);
			}
			return i;
		}
		
		public InputStream reread() {
			return new ByteArrayInputStream(baos.toByteArray());
		}
		
		@Override
		public void close() throws IOException {
			is.close();
		}
	}
	
	// different portions of identity come from different locations. Data can be persisted once everyone has weighed in? Cyclic barrier, maybe
	
	
	// addresses get farmed out, persistence can continue once all have finished: Cyclic Barrier? not really
	
	// addresses need to be sent in batches to a third-party verifier in order to lower transaction cost? okay
}
