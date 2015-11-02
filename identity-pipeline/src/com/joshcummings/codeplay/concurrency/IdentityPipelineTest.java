package com.joshcummings.codeplay.concurrency;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.junit.Test;

public class IdentityPipelineTest {
	private static class Random {
		public static double exponential(double mean) {
			return - mean * Math.log(Math.random());
		}
		
		public static void waitFor(long mean) {
			try { 
				Thread.sleep(mean / 10);//(long)exponential(mean));
			} catch ( InterruptedException e ) {
				// this is a dummy timer, so in this case, we don't care.
			}
		}
	}
	
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
			
	
	private final MalformedBatchRepository malformed = new MalformedBatchRepository() {
		
		@Override
		public void addIdentity(InputStream message, String reason) {
			Random.waitFor(1000);
		}
		
		@Override
		public void addIdentity(Identity identity, String reason) {
			Random.waitFor(1000);
		}
	};
	
	private class CappedIdentityReader implements IdentityReader {
		private Integer howMany;
		
		public CappedIdentityReader(int howMany) {
			this.howMany = howMany;
		}
		
		@Override
		public Identity read(InputStream is) {
			if ( howMany == 0 ) {
				return null;
			}
			Random.waitFor(300);
			howMany--;
			if ( howMany % 2 == 0 ) {
				throw new IllegalArgumentException("randomly can't read");
			}
			return identities.get(howMany);
		}
		
	}
	
	private final AddressVerifier av = new AddressVerifier() {

		@Override
		public boolean verify(Address address) {
			Random.waitFor(1000);
			return address.getId() % 3 == 0 || address.getId() % 2 == 0;
			//return false;//Random.exponential(2) > 1;
		}
		
	};
	
	private final PhoneNumberFormatter pnf = new PhoneNumberFormatter() {
		@Override
		public void format(Identity identity) {
			Random.waitFor(50);
		}
	};
	
	private final EmailFormatter ef = new EmailFormatter() {
		
		@Override
		public void format(Identity identity) {
			Random.waitFor(50);
		}
	};
	
	private final IdentityService is = new IdentityService() {

		@Override
		public void persistOrUpdateBestMatch(Identity identity) {
			Random.waitFor(1000);
		}
		
	};
	
	private final StatsCounter sc = new StatsCounter() {
		
		@Override
		public void countLastName(Identity identity) {
			Random.waitFor(10);
		}
		
		@Override
		public void countFirstName(Identity identity) {
			Random.waitFor(10);
		}
		
		@Override
		public void countAge(Identity identity) {
			Random.waitFor(10);
		}
	};
	
	@Test
	public void testSingleThreadedVersion() {
		IdentityPipeline ip = new IdentityPipeline(
			malformed, 
			
			Arrays.asList(
				new CappedIdentityReader(10),
				new CappedIdentityReader(11)
			),
			
			av,
			pnf,
			ef,
			is,
			sc
		);
		
		ip.process(null);
		
	}
	
	@Test
	public void testFireAndForgetMalformed() {
		IdentityPipeline ip = new IdentityPipeline(
			new MalformedBatchRepository() {
				private ExecutorService es = Executors.newCachedThreadPool();
				
				@Override
				public void addIdentity(InputStream message, String reason) {
					es.submit(() -> malformed.addIdentity(message, reason));
				}
				
				@Override
				public void addIdentity(Identity identity, String reason) {
					es.submit(() -> malformed.addIdentity(identity, reason));
				}
			}, 
			
			Arrays.asList(
				new CappedIdentityReader(10),
				new CappedIdentityReader(11)
			),
			
			av,
			pnf,
			ef,
			is,
			sc
		);
		
		ip.process(null);
		
	}
}
