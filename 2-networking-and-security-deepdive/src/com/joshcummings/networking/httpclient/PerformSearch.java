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
	private HttpClient client = HttpClientBuilder.create().build();
	
	private String searchIndividual(String query) {
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
	
	public static void main(String[] args) throws ClientProtocolException, IOException {

	}
}
