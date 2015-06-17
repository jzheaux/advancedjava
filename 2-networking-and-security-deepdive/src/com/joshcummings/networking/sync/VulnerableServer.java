package com.joshcummings.networking.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class VulnerableServer {
	
	public static void handleConnection(Socket s) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String input = br.readLine();
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			pw.println(input);
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
			try (ServerSocket ss = new ServerSocket(8080);) {
				while (true) {
					Socket s = ss.accept();
					new Thread(() ->
						VulnerableServer.handleConnection(s)
					).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
