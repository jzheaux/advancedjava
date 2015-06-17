package com.joshcummings.networking.streams;

import java.io.IOException;
import java.io.InputStream;

public class InfinitelyLongFile {
	private static class AllCapsInputStream extends InputStream {
		private InputStream is;
		
		public AllCapsInputStream(InputStream is) {
			this.is = is;
		}
		
		@Override
		public int read() throws IOException {
			char ch = (char)is.read();
			return Character.toUpperCase(ch);
		}
	}
	
	public static void main(String[] args) {
		
	}
}
