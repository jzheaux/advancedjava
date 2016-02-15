package com.joshcummings.codeplay.concurrency.single;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyingInputStream extends InputStream {
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
	
	public CopyingInputStream reread() {
		return new CopyingInputStream(new ByteArrayInputStream(baos.toByteArray()));
	}
	
	@Override
	public void close() throws IOException {
		if ( is != null ) is.close();
	}
}
