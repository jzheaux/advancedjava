package com.joshcummings.networking.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VulnerableServer {
	
	public static void handleConnection(Socket s) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			while ( !Thread.currentThread().isInterrupted() ) {
				String input = br.readLine();
				pw.println("Hello, " + input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * How is this server vulnerable to a DoS attack? How can it be protected from it?
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// use executor Service instead 
		// of instantiating a new thread each time
		ExecutorService es = Executors.newFixedThreadPool(1000);
		try (ServerSocket ss = new ServerSocket(8080);) {
			System.out.println("Server is running...");
			while (true) {
				Socket s = ss.accept();
				es.submit(() ->
					VulnerableServer.handleConnection(s)
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
