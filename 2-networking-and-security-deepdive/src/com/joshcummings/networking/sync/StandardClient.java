package com.joshcummings.networking.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StandardClient {
	public static void main(String[] args) {
		/**
		 * Make sure the Server is running first.
		 */
		try ( Socket s = new Socket("localhost", 8080) ) {
			PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
			pw.println("Something");
			BufferedReader br = new BufferedReader(
					new InputStreamReader(s.getInputStream()));
			System.out.println(br.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
