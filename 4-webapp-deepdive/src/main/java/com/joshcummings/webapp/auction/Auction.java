package com.joshcummings.webapp.auction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import com.joshcummings.webapp.profile.User;

public class Auction {
	private final Long id;
	private final String title;
	private final User owner;
	private final BigDecimal price;
	private final String shortDescription;
	private final String imageLocation;
	private final Instant startTime;
	private final Instant endTime;
	
	public Auction(Long id, String title, User owner) {
		this(id, title, owner, new BigDecimal(".01"), title, "");
	}
	
	public Auction(Long id, String title, User owner, BigDecimal price,
			String shortDescription, String imageLocation, Instant startTime,
			Instant endTime) {
		this.id = id;
		this.title = title;
		this.owner = owner;
		this.price = price;
		this.shortDescription = shortDescription;
		this.imageLocation = imageLocation;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Auction(Long id, String title, User owner, BigDecimal price,
			String shortDescription, String imageLocation) {
		this(id, title, owner, price, shortDescription, imageLocation, Instant.now(), Instant.now().plus(Period.ofDays(7)));
	}

	public static Auction withBid(Auction a, BigDecimal price) {
		return new Auction(a.getId(), a.getTitle(), a.getOwner(), price,
				a.getShortDescription(), a.getImageLocation(), a.getStartTime(),
				a.getEndTime());
	}

	public Long getTimeLeft() {
		return Instant.now().until(endTime, ChronoUnit.MILLIS);
	}


	public Long getId() {
		return id;
	}


	public String getTitle() {
		return title;
	}

	public User getOwner() {
		return owner;
	}
	
	public BigDecimal getPrice() {
		return price.setScale(2, BigDecimal.ROUND_HALF_UP);
	}


	public String getShortDescription() {
		return shortDescription;
	}


	public String getImageLocation() {
		return imageLocation;
	}


	public Instant getStartTime() {
		return startTime;
	}


	public Instant getEndTime() {
		return endTime;
	}
}
