package com.joshcummings.webapp.auction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class AuctionSearchBean {
	private String query;
	private Set<Auction> results = new HashSet<Auction>();
	
	@Inject AuctionService auctionService;
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public List<Auction> getResults() {
		return new ArrayList<>(results);
	}
	
	public void doSearch() {
		results = auctionService.findAllBySearchQuery(query);
	}
}
