package com.joshcummings.async.poor;

import java.util.function.Consumer;

public class PoorMansAsync {
	/**
	 * This strategy make the method non-blocking; it will return immediately,
	 * even though the thread is doing work.
	 * 
	 * @param data
	 * @param c
	 */
	public void dontCallUsWellCallYou(String data, Consumer<Integer> c) {
		new Thread(() -> {
			for ( int i = 0; i < 10000000; i++ ) {
				i+=0;
			}
			c.accept(24);
		}).start();
	}
}
