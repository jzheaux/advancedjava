package com.joshcummings.codeplay.concurrency;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.junit.Test;

import com.joshcummings.codeplay.concurrency.aggregation.LockableStatsLedger;
import com.joshcummings.codeplay.concurrency.aggregation.ThreadedIdentityService;
import com.joshcummings.codeplay.concurrency.fireandforget.ProducerMalformedIdentityRepository;
import com.joshcummings.codeplay.concurrency.fireandforget.ThreadPoolExecutorMalformedIdentityRepository;
import com.joshcummings.codeplay.concurrency.single.IdentityPipeline;
import com.joshcummings.codeplay.concurrency.single.MultiStrategyIdentityReader;
import com.joshcummings.codeplay.concurrency.single.SimpleStatsLedger;
import com.joshcummings.codeplay.concurrency.splitting.ShortCircuitingMultiStrategyIdentityReader;
import com.joshcummings.codeplay.concurrency.throttle.BatchingAddressVerifier;

public class IdentityPipelineTest {
	private static class Generator {
		private Random random = new Random(29837420394L);
		
		public static double exponential(double mean) {
			return - mean * Math.log(Math.random());
		}
		
		public static void waitFor(long mean) {
			try { 
				Thread.sleep(mean);//(long)exponential(mean));
			} catch ( InterruptedException e ) {
				// this is a dummy timer, so in this case, we don't care.
			}
		}
	}
	
	private final MalformedIdentityRepository malformed = new MalformedIdentityRepository() {
		
		@Override
		public void addIdentity(InputStream message, String reason) {
			System.out.println("Logging malformed identity: " + reason);
			Generator.waitFor(1000);
		}
		
		@Override
		public void addIdentity(Identity identity, String reason) {
			Generator.waitFor(1000);
		}
	};
	
	private class CappedIdentityReader implements IdentityReader {
		private AtomicInteger howMany;
		private CountDownLatch cdl;
		
		private final List<Identity> identities = new ArrayList<>();
		
		{ 
			for ( int i = 0; i < 1000; i++ ) {
				identities.add(new Person("bobs", "yeruncle".toCharArray(),
						"Clarence Witherspoon", "801-555-1212", "bobs@yeruncle.com",
						Arrays.asList(
							new Address("555 Main Street", "Salt Lake City", "UT", "84101"),
							new Address("1600 Pennsylvania Avenue", "Washington", "D.C.", "10000"),
							new Address("1 Infinite Loop", "San Jose", "CA", "94000")
						)));
			}
		}
		
		public CappedIdentityReader(int howMany) {
			this.howMany = new AtomicInteger(howMany);
		}
		
		public CappedIdentityReader(int howMany, CountDownLatch cdl) {
			this(howMany);
			
			this.cdl = cdl;
		}
		
		@Override
		public Identity read(InputStream is) {
			synchronized ( howMany ) {
				if ( howMany.get() == 0 ) {
					return null;
				}
				Generator.waitFor(300);
				howMany.decrementAndGet();
				if ( cdl != null ) cdl.countDown();
				if ( howMany.get() % 2 == 0 ) {
					throw new IllegalArgumentException("randomly can't read");
				}
				System.out.println("Returning identity #" + identities.get(howMany.get()).getId());
				return identities.get(howMany.get());
			}
		}
		
	}
	
	private final AddressVerifier av = new AddressVerifier() {

		@Override
		public void verify(List<Address> addresses) {
			Generator.waitFor(1000);
			for ( Address address : addresses ) {
				address.setVerified(address.getId() % 3 == 0 || address.getId() % 2 == 0);
			}
			//return false;//Random.exponential(2) > 1;
		}
		
	};
	
	private final PhoneNumberFormatter pnf = new PhoneNumberFormatter() {
		@Override
		public void format(Identity identity) {
			Generator.waitFor(50);
		}
	};
	
	private final EmailFormatter ef = new EmailFormatter() {
		
		@Override
		public void format(Identity identity) {
			Generator.waitFor(50);
		}
	};
	
	private final IdentityService is = new IdentityService() {

		@Override
		public boolean persistOrUpdateBestMatch(Identity identity) {
			Generator.waitFor(1000);
			return Generator.exponential(.5) > .5;
		}
		
		@Override
		public List<Identity> search(Predicate<Identity> pred) {
			return null;
		}
		
	};
	
	private final StatsLedger sc = new StatsLedger() {
		
		@Override
		public void recordEntry(StatsEntry entry) {
			Generator.waitFor(10);
		}
		
		
		@Override
		public Integer getRecordCount() {
			return 0;
		}
	};
	
	@Test
	public void testHappyPath() {
		MalformedIdentityRepository malformed =
				new ProducerMalformedIdentityRepository(this.malformed);
		IdentityPipeline ip = new IdentityPipeline(
			malformed, 
			
			new MultiStrategyIdentityReader(Arrays.asList(
				new CappedIdentityReader(10),
				new CappedIdentityReader(11)
			), malformed),
			
			av,
			pnf,
			ef,
			is,
			sc
		);
		
		ip.process(null);
		
	}
	
	@Test
	public void testMultiThreadedHappyPath() {
		CountDownLatch cdl = new CountDownLatch(10);
		IdentityService is = new ThreadedIdentityService();
		MalformedIdentityRepository malformed = malformedBatchRepository();
		com.joshcummings.codeplay.concurrency.dependency.IdentityPipeline ip = 
				new com.joshcummings.codeplay.concurrency.dependency.IdentityPipeline(
			malformed, 
			new ShortCircuitingMultiStrategyIdentityReader(Arrays.asList(
				new CappedIdentityReader(10, cdl),
				new CappedIdentityReader(11, cdl)
			), malformed),
			new BatchingAddressVerifier(av, 10, 1000),
			pnf,
			ef,
			is,
			new LockableStatsLedger(new SimpleStatsLedger())
		);
		
		ip.process(null);
		
		try {
			cdl.await();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		
		//List<Identity> identity = is.search(id -> true);
		//System.out.println(identity.size());
	}
	
	public MalformedIdentityRepository malformedBatchRepository() {
		return new ProducerMalformedIdentityRepository(malformed);
	}
}
