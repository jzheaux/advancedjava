package com.joshcummings.ws.auction;

public class BidTooLowException extends RuntimeException {
	public BidTooLowException(String message) {
		super(message);
	}
}
