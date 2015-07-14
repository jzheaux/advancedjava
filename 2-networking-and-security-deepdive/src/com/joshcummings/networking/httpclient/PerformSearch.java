package com.joshcummings.networking.httpclient;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class PerformSearch {
	private static HttpClient client = HttpClientBuilder.create().build();
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String results =
				Stream.of("http://www.google.com?q=Java",
						  "http://www.ask.com/web?q=Java",
						  "http://www.yahoo.com/search?p=Java")
					.parallel()
					.map(
						(query) -> {
							return searchIndividual(query);
						})
					.map(
						(html) -> {
							if ( html.startsWith("<!doctype") ) {
								return "well-formed!";
							} else {
								return "mal-formed!";
							}
						}
					)
					.map(
						(report) -> report.toString() + "!"
					)
					.findAny().get();
		
		System.out.println(results);
	}
	
	private static String searchIndividual(String query) {
		try {
			HttpGet get = new HttpGet(query);
			HttpResponse response = client.execute(get);
			return IOUtils.toString(response.getEntity().getContent());
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public String searchGoogle(String query) {
		return searchIndividual("http://www.google.com?q=" + query);
	}
	
	public String searchAsk(String query) {
		// implement this - make it real!
		return null;
	}
	
	public String searchYahoo(String query) {
		// implement this - make it real!
		return null;
	}

}
