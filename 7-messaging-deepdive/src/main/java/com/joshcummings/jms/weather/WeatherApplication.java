package com.joshcummings.jms.weather;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

@SpringBootApplication
@EnableJms
public class WeatherApplication {
	@Bean
	public Topic weatherTopic() {
		ActiveMQTopic t = new ActiveMQTopic("weather-topic");
		return t;
	}
	
	@Bean // Strictly speaking this bean is not necessary as boot creates a default
    JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(WeatherApplication.class, args);
		
		WeatherStream ws = context.getBean(WeatherStream.class);
		ws.begin();
	}
}
