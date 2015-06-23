package com.joshcummings.ws.auction;

import java.math.BigDecimal;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.web.client.RestTemplate;

@Named
@ApplicationScoped
public class RestTemplateAuctionService implements AuctionService {
	@Inject RestTemplate restTemplate;
	@Inject @Config String serverAddress;
	
	@Override
	public Auction find(Long id) {
		Auction a = restTemplate.getForObject(serverAddress + "/auction" + id, Auction.class);
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

}
