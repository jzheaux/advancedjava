package com.joshcummings.ws.auction;

import java.math.BigDecimal;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/auction")
@Produces("application/json")
public interface AuctionService {
	@GET
	@Path("/{id}")
	Auction find(@PathParam("id") Long id);
	
	@GET
	@Path("/all")
	Set<Auction> findAll();
	
	@GET
	@Path("/any")
	Set<Auction> findAllBySearchQuery(@QueryParam("q") String query);
	
	@GET
	@Path("/by-price")
	Set<Auction> findAllUnderSpecifiedPrice(
			@QueryParam("price") Double price);
	
	
	
	
	
	
	
	
	
	
	
	
	
	@POST
	@Path("/{id}/bid")
	@Consumes("application/json")
	Auction placeBid(@PathParam("id") Long id, BigDecimal amount);
	
	
	
	
	
	
	
	
	
}
