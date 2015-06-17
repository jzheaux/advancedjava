package com.joshcummings.networking.serialization;
import java.io.IOException;
import java.io.InputStream;


public class DecryptingInputStream extends InputStream {
	private InputStream is;
	
	public DecryptingInputStream(InputStream is) {
		this.is = is;
	}
	
	@Override
	public int read() throws IOException {
		int read = is.read();
		if ( read == -1 ) {
			return read;
		}
		return (byte)read - 1;
	}

}
