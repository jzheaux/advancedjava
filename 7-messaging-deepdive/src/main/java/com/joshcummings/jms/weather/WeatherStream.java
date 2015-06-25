package com.joshcummings.jms.weather;

import java.util.Random;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Topic;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class WeatherStream {
	private volatile boolean sendWeatherData = true;
	
	private final JmsTemplate jmsTemplate;
	private final Topic weatherTopic;
	
	@Inject
	public WeatherStream(JmsTemplate jmsTemplate, Topic weatherTopic) {
		this.jmsTemplate = jmsTemplate;
		this.weatherTopic = weatherTopic;
	}
	
	
	/**
	 * Perpetually send weather data to those listening
	 */
	public void begin() {
		Random r = new Random();
		Stream.iterate(new Double(83.2421), (previous) -> new Double(previous + r.nextDouble() - 0.5))
			.forEach((temperature) -> {
				jmsTemplate.send(weatherTopic, (session) -> {
					Message m = session.createObjectMessage(temperature);
					
					/**
					 * In the message, we can pass properties that allow listeners to select
					 * whether or read or to discard.
					 */
					m.setStringProperty("weatherTower", "Nashville");
					
					return m;
				});
				
				try {
					Thread.sleep(r.nextInt(3000));
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
				}
			});
	}
	
	public void end() {
		sendWeatherData = true;
	}
}
