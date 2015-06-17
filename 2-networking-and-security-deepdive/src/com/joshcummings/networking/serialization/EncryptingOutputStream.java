package com.joshcummings.networking.serialization;
import java.io.IOException;
import java.io.OutputStream;


public class EncryptingOutputStream extends OutputStream {
	private OutputStream os;
	
	public EncryptingOutputStream(OutputStream os) {
		this.os = os;
	}

	@Override
	public void write(int b) throws IOException {
		os.write((byte)b + 1);
	}

}
