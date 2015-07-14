package com.joshcummings.jms.weather;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class WeatherListener {
	/**
	 * Instead of just using the annotation here, we
	 * can configure a DefaultMessageListenerContainer which has more features.
	 * @param weather
	 */
	
	@JmsListener(destination="weather-topic", containerFactory="myJmsContainerFactory",
			selector="weatherTower = 'Nashville'", concurrency="5-10"
			/*, subscription="durable"*/
			)
	public void weatherReceived(Double weather) {
		System.out.println("Thread #" + Thread.currentThread());
		System.out.println("Current high for today: " + weather);
	}
	
}
