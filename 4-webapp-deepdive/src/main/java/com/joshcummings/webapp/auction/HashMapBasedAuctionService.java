package com.joshcummings.webapp.auction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.joshcummings.webapp.profile.User;

@Named
@ApplicationScoped
public class HashMapBasedAuctionService implements AuctionService {
	private Map<Long, Auction> auctions = new HashMap<>();
	{
		User owner = new User(1L, "bobs", "yeruncle".toCharArray());
		auctions.put(1L, new Auction(1L, "Toaster", owner, new BigDecimal(14.99), "This is quite literally the best toaster in the developed world. Throwing piles of money at the developed world's toaster problems will be to no avail. Buy this one and be happy that you don't need to toast it on a rock in your backyard.", "http://ak1.ostkcdn.com/images/products/7617987/7617987/Kalorik-2-slice-Stainless-Steel-Toaster-P15039128.jpeg"));
	}
	
	@Override
	public Auction find(Long id) {
		return auctions.get(id);
	}

	@Override
	public Set<Auction> findAll() {
		return new HashSet<>(auctions.values());
	}

	@Override
	public Set<Auction> findAllBySearchQuery(String query) {
		return auctions
					.values().stream()
					.filter((auction) -> {
						return auction.getShortDescription().contains(query);
					}).collect(Collectors.toSet());
	}

	@Override
	public Auction placeBid(Long id, BigDecimal amount) {
		Auction a = find(id);
		if ( amount.compareTo(a.getPrice()) > 0 ) {
			a = Auction.withBid(a, amount);
			auctions.put(a.getId(), a);
		}
		return find(a.getId());
	}

}
