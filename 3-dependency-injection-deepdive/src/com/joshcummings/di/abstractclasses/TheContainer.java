package com.joshcummings.di.abstractclasses;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TheContainer {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "com/joshcummings/di/abstractclasses/abstract.xml" });
		SampleImplementation si = context.getBean("implementation", SampleImplementation.class);
		
	}
}
