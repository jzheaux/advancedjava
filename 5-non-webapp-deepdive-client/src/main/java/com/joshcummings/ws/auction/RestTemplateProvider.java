package com.joshcummings.ws.auction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.springframework.web.client.RestTemplate;


public class RestTemplateProvider {
	@Produces
	@ApplicationScoped
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
