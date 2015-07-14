package com.joshcummings.networking.ssl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Stream;



public class GoodNonBrowserHttps {
	public static void main(String[] args) throws MalformedURLException, IOException {
		// Show how to store ssl key, how to do handshake with http-client
		URLConnection c = new URL("https://www.google.com").openConnection();
		try ( BufferedReader br =
				new BufferedReader(
				new InputStreamReader(c.getInputStream()));
			  Stream<String> lines = br.lines(); ) {
			lines.forEach(System.out::println);
		}
	}
}
