package com.joshcummings.di.instantiation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TheContainer {
	public static void main(String[] args) {
		ApplicationContext context = 
			new ClassPathXmlApplicationContext(
				new String[] { 
					"com/joshcummings/di/instantiation/by-constructor.xml" });
		NumberGenerator ng = context.getBean("numberGenerator", NumberGenerator.class);
		
		context = new ClassPathXmlApplicationContext(new String[] { "com/joshcummings/di/instantiation/by-setters.xml" });
		ng = context.getBean("numberGenerator", NumberGenerator.class);
		
		context = new ClassPathXmlApplicationContext(new String[] { "com/joshcummings/di/instantiation/by-static-factory.xml" });
		ng = context.getBean("numberGenerator", NumberGenerator.class);
		
		context = new ClassPathXmlApplicationContext(new String[] { "com/joshcummings/di/instantiation/by-instance-factory.xml" });
		ng = context.getBean("numberGenerator", NumberGenerator.class);
		ng = context.getBean("basicNumberGenerator", NumberGenerator.class);
		ng = context.getBean("primeNumberGenerator", NumberGenerator.class);
		
		NumberGenerator def = context.getBean("defaultNumberGenerator",
				NumberGenerator.class);
		
		System.out.println("You did it!");
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
