package com.joshcummings.codeplay.concurrency.splitting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedIdentityRepository;

public class ShortCircuitingMultiStrategyIdentityReader implements
		IdentityReader {
	private IdentityReader primary;
	private List<IdentityReader> readers;
	private MalformedIdentityRepository malformed;
	
	private ExecutorService es = Executors.newCachedThreadPool();

	public ShortCircuitingMultiStrategyIdentityReader(List<IdentityReader> readers, MalformedIdentityRepository repository) {
		this.primary = readers.stream().findFirst().orElseThrow(IllegalArgumentException::new);
		this.readers = readers.subList(1, readers.size());
		this.malformed = repository;
	}
	
	@Override
	public Identity read(InputStream is) {
		try ( CopyingInputStream cis = new CopyingInputStream(is); ) {
			try {
				return primary.read(cis);
			} catch ( Exception e ) {
				if ( !readers.isEmpty() ) {
					ExecutorCompletionService<Identity> ecs = new ExecutorCompletionService<Identity>(es);
					for ( IdentityReader reader : readers ) {
						ecs.submit(() ->
						{ 
							try {
								return reader.read(cis.reread());
							} catch ( Exception f ) {
								return new BadIdentity();
							}
						});
					}
					
					try {
						for ( IdentityReader reader : readers ) {
							Identity i = ecs.take().get();
							if ( !(i instanceof BadIdentity) ) {
								return i;
							}
						}
					} catch ( Exception f ) {
						// fall through to below
					}
				}
				malformed.addIdentity(cis.reread(), "Tried all serialization strategies and could not evaluate the identity.");
				return read(is);
			}
		} catch ( IOException e ) {
			throw new IllegalStateException("Something terrible happened with the re-read stream", e);
		}
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
		}
	}
}
