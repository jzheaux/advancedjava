package com.joshcummings.webapp.auction;

import java.math.BigDecimal;
import java.util.Set;

public interface AuctionService {
	Auction find(Long id);
	
	Set<Auction> findAll();
	
	Set<Auction> findAllBySearchQuery(String query);
	
	Auction placeBid(Long id, BigDecimal amount);
}
