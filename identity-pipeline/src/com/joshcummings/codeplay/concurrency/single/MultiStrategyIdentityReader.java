package com.joshcummings.codeplay.concurrency.single;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.joshcummings.codeplay.concurrency.Identity;
import com.joshcummings.codeplay.concurrency.IdentityReader;
import com.joshcummings.codeplay.concurrency.MalformedIdentityRepository;


public class MultiStrategyIdentityReader implements IdentityReader {
	protected final IdentityReader primary;
	protected final List<IdentityReader> readers;
	protected final MalformedIdentityRepository repository;
	
	public MultiStrategyIdentityReader(List<IdentityReader> readers, MalformedIdentityRepository repository) {
		this.primary = readers.stream().findFirst().orElseThrow(IllegalArgumentException::new);
		this.readers = readers.subList(1, readers.size());
		this.repository = repository;
	}
	
	@Override
	public Identity read(InputStream is) {
		try ( CopyingInputStream cis = new CopyingInputStream(is); ) {
			try {
				return primary.read(cis);
			} catch ( Exception e ) {
				for ( IdentityReader reader : readers ) {
					try {
						return reader.read(cis.reread());
					} catch ( Exception f ) {
						// try another one
					}
				}
				
				repository.addIdentity(cis.reread(), "Tried all identity serialization strategies and all failed");
			}
		} catch ( IOException e ) {
			throw new IllegalStateException("Something terrible happened with the re-read stream", e);
		}

		return read(is);
	}

}
