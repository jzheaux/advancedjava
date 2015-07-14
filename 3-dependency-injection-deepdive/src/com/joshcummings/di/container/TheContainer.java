package com.joshcummings.di.container;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.joshcummings.di.gumballs.GumballMachineService;
import com.joshcummings.di.gumballs.PurchaseService;

public class TheContainer {
	public static void main(String[] args) {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext(
						new String[] { 
								"com/joshcummings/di/container/services.xml", 
								"com/joshcummings/di/container/daos.xml" });
		
		GumballMachineService gms =
				context
					.getBean("gumballMachineService", GumballMachineService.class);
		PurchaseService ps = 
				context
					.getBean("purchaseService", PurchaseService.class);
		
		System.out.println("You did it!");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
