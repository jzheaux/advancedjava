package com.joshcummings.badvisibility;

public class NoVisibility {
	private volatile static boolean ready;
	private static int number;
	
	private static class ReaderThread extends Thread {
		public void run() {
			while (!ready) {
			}
			if ( number == 0 ) {
				System.out.println("WHAT?");
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		//for ( int i = 0; i < 100000; i++ ) {
			number = 0;
			ready = false;
			Thread t = new ReaderThread();
			t.start();
			//Thread.sleep(1000);
			number = 42;
			ready = true;
			t.join();
			//System.out.println(i + " completed");
		//}
	}
}
