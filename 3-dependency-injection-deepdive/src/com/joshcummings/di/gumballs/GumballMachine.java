package com.joshcummings.di.gumballs;

import java.util.HashSet;
import java.util.Set;

public class GumballMachine {
	private final Long id;
	
	private final User owner;
	
	private final Double price;
	
	private final Set<Gumball> candy = new HashSet<Gumball>();

	private volatile Double money;
	
	public GumballMachine(Long id, Double price, User owner) {
		this.id = id;
		this.price = price;
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public User getOwner() {
		return owner;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public synchronized void addCandy(Set<Gumball> gumballs) {
		candy.addAll(gumballs);
	}
	
	public synchronized Gumball purchase(Double money) {
		Gumball g = candy.iterator().next();
		this.money -= money;
		candy.remove(g);
		return g;
	}

	public boolean accept(MachineKey mk) {
		return true;
	}

	public synchronized Double removeMoney() {
		Double toReturn = money;
		money = 0.0;
		return toReturn;
	}
}
