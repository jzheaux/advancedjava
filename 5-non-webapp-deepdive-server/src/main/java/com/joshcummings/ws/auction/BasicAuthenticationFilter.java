package com.joshcummings.ws.auction;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

@Provider
@PreMatching
public class BasicAuthenticationFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		String authorization = requestContext.getHeaderString("Authorization");
		if ( authorization == null ) {
			requestContext.abortWith(Response.status(401).build());
		} else {
			String[] parts = authorization.split(" ");
			if ( "Basic".equals(parts[0]) ) {
				byte[] decoded = Base64.decode(parts[1].getBytes());
				String[] up = new String(decoded).split(":");
				if ( !up[0].equals("bobs") || !up[1].equals("yeruncle")) {
					requestContext.abortWith(Response.status(403).build());
				}
			} else {
				requestContext.abortWith(Response.status(400).build());
			}
		}
	}

}
