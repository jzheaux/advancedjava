package com.joshcummings.ws.auction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

@Named
@ApplicationScoped
@Provider
@PreMatching
public class ETagResolutionFilter implements ContainerRequestFilter,
		ContainerResponseFilter {
	private static final Map<String, Long> uriToLastModified = new HashMap<>();
	
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		if ( responseContext.hasEntity() && responseContext.getEntity() instanceof Auction ) {
			Auction entity = (Auction)responseContext.getEntity();
			responseContext.getHeaders().add("ETag", 
				new EntityTag(String.valueOf(entity.getLastModified())));
			synchronized ( uriToLastModified ) {
				uriToLastModified.put(getResourceRoot(requestContext), entity.getLastModified());
			}
		}
	}

	private String getResourceRoot(ContainerRequestContext requestContext) {
		String resource = requestContext.getUriInfo().getPath();
		if ( resource.endsWith("bid") ) { // ewww
			return resource.substring(0, resource.indexOf("/bid"));
		}
		return resource;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		Long lastModified = null;
		synchronized ( uriToLastModified ) {
			lastModified = uriToLastModified.get(getResourceRoot(requestContext));
		}

		if ( lastModified != null ) {
			EntityTag eTag = new EntityTag(String.valueOf(lastModified));
			ResponseBuilder rb = requestContext.getRequest().evaluatePreconditions(eTag);
			if ( rb != null ) {
				requestContext.abortWith(rb.build());
			}
		}
	}

}
