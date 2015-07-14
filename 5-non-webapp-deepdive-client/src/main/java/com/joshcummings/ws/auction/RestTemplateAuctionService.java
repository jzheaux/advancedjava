package com.joshcummings.ws.auction;

import java.math.BigDecimal;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Named
@ApplicationScoped
public class RestTemplateAuctionService implements AuctionService {
	@Inject RestTemplate restTemplate;
	@Inject @Config String serverAddress;
	
	@Override
	public Auction find(Long id) {
		Auction a = restTemplate
				.getForObject(
						serverAddress + "/auction/" + id
						, Auction.class);
		return a;
	}

	@Override
	public Set<Auction> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Auction> findAllBySearchQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Auction placeBid(Long id, BigDecimal amount) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<Auction> findAllUnderSpecifiedPrice(Double price) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		RestTemplate rt = new RestTemplate();

		String url = "http://localhost:8080/non-webapp-deepdive-server/rest/auction/1";
		ResponseEntity<String> response =
				rt.getForEntity(url, String.class);
		System.out.println(response.getBody());

		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");

		HttpEntity<Object> request = 
				new HttpEntity<Object>(
						"{ \"amount\" : \"20.99\" }",
						header);
		
		url = "http://localhost:8080/non-webapp-deepdive-server/rest/auction/1/bid";
		response =
				rt.postForEntity(url, request, String.class);
		System.out.println(response.getBody());
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}


}
