package com.joshcummings.cdi.events;

import javax.enterprise.event.Observes;
import javax.inject.Named;

@Named
public class NeedsToKnowAboutSuccessfulLoginEvents {
	/**
	 * @Observes causes this method to be called whenever a SuccessfulLoginEvent is fired
	 * 
	 */
	public void successfulLoginOccurred(@Observes SuccessfulLoginEvent event) {
		System.out.println("Login sucess: [" + event.getUsername() + "]");
	}
}
