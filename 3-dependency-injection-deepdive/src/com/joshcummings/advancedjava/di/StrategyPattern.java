package com.joshcummings.advancedjava.di;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class StrategyPattern {
	public static void main(String[] args) {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		Interface i = context.getBean("strategy", Interface.class);
		Interface j = context.getBean("strategy", Interface.class);
		System.out.println(i.sayHello());
		System.out.println(i == j);
		
		Interface k = context.getBean("prototypeStrategy", Interface.class);
		Interface l = context.getBean("prototypeStrategy", Interface.class);
		System.out.println(k == l);
		
		
	}
}
