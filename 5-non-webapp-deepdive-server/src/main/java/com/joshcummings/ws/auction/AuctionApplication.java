package com.joshcummings.ws.auction;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@ApplicationPath("/rest")
public class AuctionApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(HashMapBasedAuctionService.class);
        classes.add(JacksonJsonProvider.class);
        classes.add(BidTooLowExceptionMapper.class);
        //classes.add(BasicAuthenticationFilter.class);
        classes.add(ETagResolutionFilter.class);
        
        return classes;
	}
}
