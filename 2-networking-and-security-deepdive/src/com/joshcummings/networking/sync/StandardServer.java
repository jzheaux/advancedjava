package com.joshcummings.networking.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StandardServer {
	public static void main(String[] args) {
		ExecutorService connectPool = Executors.newFixedThreadPool(10);

		new Thread(() -> {
			try (ServerSocket ss = new ServerSocket(8080);) {
				while (true) {
					Socket s = ss.accept();
					connectPool.submit(() -> {
						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
							String input = br.readLine();
							PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
							pw.println(input);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
