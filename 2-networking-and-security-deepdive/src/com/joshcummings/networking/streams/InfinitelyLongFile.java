package com.joshcummings.networking.streams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class InfinitelyLongFile {
	private static class AllCapsInputStream extends InputStream {
		private InputStream is;
		
		public AllCapsInputStream(InputStream is) {
			this.is = is;
		}
		
		@Override
		public int read() throws IOException {
			int ch = is.read();
			if ( ch == -1 ) {
				return ch;
			} else {
				return Character.toUpperCase(ch);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("favoritefoods.txt");
		
		FileInputStream fis = new FileInputStream(file);
		AllCapsInputStream acis = new AllCapsInputStream(fis);
		
		String lines = IOUtils.toString(acis);
		
		System.out.println(lines);
		
		
		
		
		
		
		
		
	}
}
