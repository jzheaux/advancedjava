package com.joshcummings.codeplay.concurrency.splitting;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {
	public static void main(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();
		
		Future<String> message = es.submit(() -> { 
			try {
				Thread.sleep(4000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "one is done!";
		});
		
		es.submit(() -> {
			try {
				String m = message.get();
				System.out.println("Got message: " + m);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		});
		
		es.submit(() -> {
			try {
				String m = message.get();
				System.out.println("Got message: " + m);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		});
	}
}
