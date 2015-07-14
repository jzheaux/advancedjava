package com.joshcummings.ws.auction;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BidTooLowExceptionMapper implements ExceptionMapper<BidTooLowException> {

	/**
	 * This response is not particularly informative.
	 * 
	 * Use the Response builder methods to add an entity that provides more context as to what failed.
	 * 
	 */
	@Override
	public Response toResponse(BidTooLowException exception) {
		return Response.status(400).build();
	}

}
