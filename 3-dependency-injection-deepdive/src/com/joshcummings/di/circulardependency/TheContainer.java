package com.joshcummings.di.circulardependency;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TheContainer {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "com/joshcummings/di/circulardependency/circular.xml" });
		BeanA a = context.getBean("a", BeanA.class);
		BeanB b = context.getBean("b", BeanB.class);
	}
}
