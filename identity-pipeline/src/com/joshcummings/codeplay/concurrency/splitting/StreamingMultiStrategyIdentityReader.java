package com.joshcummings.codeplay.concurrency.splitting;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.joshcummings.codeplay.concurrency.BadIdentity;
import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedBatchRepository;
import com.joshcummings.codeplay.concurrency.single.MultiStrategyIdentityReader;

public class StreamingMultiStrategyIdentityReader extends MultiStrategyIdentityReader {
	private ExecutorService es = Executors.newCachedThreadPool();

	public StreamingMultiStrategyIdentityReader(List<IdentityReader> readers, MalformedBatchRepository repository) {
		super(readers, repository);
	}
	
	@Override
	public Identity read(InputStream is) {
		try ( CopyingInputStream cis = new CopyingInputStream(is); ) {
			try {
				return primary.read(cis);
			} catch ( Exception e ) {
				if ( !readers.isEmpty() ) {
					// This isn't perfectly equivalent to the short-circuiting example seen previously.
					// In this setup, the reader will be called for all of them and then the
					// filter will be called on the entire mapped stream.
					Optional<Identity> i = readers.parallelStream()
							.map(reader -> {
									try {
										return reader.read(cis.reread());
									} catch ( Exception f ) {
										return new BadIdentity();
									}
								})
							.filter(identity -> !(identity instanceof BadIdentity))
							.findAny();
					if ( i.isPresent() ) {
						return i.get();
					}
				}
				repository.addIdentity(cis.reread(), "Tried all serialization strategies and could not evaluate the identity.");
				return read(is);
			}
		} catch ( IOException e ) {
			throw new IllegalStateException("Something terrible happened with the re-read stream", e);
		}
	}
}