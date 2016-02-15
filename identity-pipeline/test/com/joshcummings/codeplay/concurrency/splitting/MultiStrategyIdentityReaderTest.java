package com.joshcummings.codeplay.concurrency.splitting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.Generator;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedIdentityRepository;
import com.joshcummings.codeplay.concurrency.Person;

public class MultiStrategyIdentityReaderTest {
	private MalformedIdentityRepository malformed = new MalformedIdentityRepository() {
		@Override
		public void addIdentity(Identity identity, String reason) {}

		@Override
		public void addIdentity(InputStream message, String reason) {}
	};
	
	private static class RandomIdentityReader implements IdentityReader {
		private int index;
		
		private RandomIdentityReader(int index) {
			this.index = index;
		}
		@Override
		public Identity read(InputStream is) {
			try {
				int i = is.read();
				Generator.waitFor(i - 48 == index ? 100 : 1000);
				return i - 48 == index ? new Person(null, null, null, null, null, Collections.emptyList()) : new BadIdentity();
			} catch (IOException e) {
				return new BadIdentity();
			}
		}
	};

	private List<IdentityReader> readers = Arrays.asList(
			new RandomIdentityReader(1), new RandomIdentityReader(2),
			new RandomIdentityReader(3), new RandomIdentityReader(4),
			new RandomIdentityReader(5));

	private InputStream bais;

	private static final Integer NUM_IDENTITIES = 5;

	@Before
	public void setUp() {
		bais = new ByteArrayInputStream("125452342351245234512351234521452".getBytes());
	}

	@Test
	public void testSingleThreaded() {
		ScatterGatherer sg = new SingleThreadedScatterGatherer();
		MultiStrategyIdentityReader reader = new MultiStrategyIdentityReader(readers, sg, malformed);
		for (int i = 0; i < NUM_IDENTITIES; i++) {
			System.out.println("Identity #" + i);
			reader.read(bais);
		}
	}

	@Test
	public void testExecutorService() {
		ScatterGatherer sg = new ExecutorServiceScatterGatherer();
		MultiStrategyIdentityReader reader = new MultiStrategyIdentityReader(readers, sg, malformed);
		for (int i = 0; i < NUM_IDENTITIES; i++) {
			System.out.println("Identity #" + i);
			reader.read(bais);
		}
	}

	@Test
	public void testExecutorCompletionService() {
		ScatterGatherer sg = new ExecutorCompletionServiceScatterGatherer();
		MultiStrategyIdentityReader reader = new MultiStrategyIdentityReader(readers, sg, malformed);
		for (int i = 0; i < NUM_IDENTITIES; i++) {
			System.out.println("Identity #" + i);
			reader.read(bais);
		}
	}
}
