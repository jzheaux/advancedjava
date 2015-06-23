package com.joshcummings.webapp.auction;

import java.math.BigDecimal;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.joshcummings.ws.auction.Auction;
import com.joshcummings.ws.auction.AuctionService;

@Named
@RequestScoped
public class AuctionViewBean {
	private Long id;
	private String title;
	private String shortDescription;
	private BigDecimal price;
	private Long millisLeft;
	private String imageLocation;
	
	@Inject AuctionService auctionService;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public BigDecimal getPrice() {
		return price;
	}
	
	public BigDecimal getBidPrice() {
		return price.add(BigDecimal.ONE);
	}

	public void setBidPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getMillisLeft() {
		return millisLeft;
	}

	public String getImageLocation() {
		return imageLocation;
	}
	
	public void show() {
		Auction a = auctionService.find(id);
		title = a.getTitle();
		shortDescription = a.getShortDescription();
		price = a.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP);
		millisLeft = a.getTimeLeft();
		imageLocation = a.getImageLocation();
	}
	
	public void placeBid() {
		auctionService.placeBid(id, price);
	}
}
