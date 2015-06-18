package com.joshcummings.di.gumballs;

import java.util.List;

public class GumballMachineService {
	private GumballMachineDao machineDao = new GumballMachineDao();
	private MachineKeyDao keyDao = new MachineKeyDao();
	
	private PurchaseService purchaseService = new PurchaseService();
	
	public Gumball purchaseGumball(Long gumballMachineId, Double money, Long userId) {
		GumballMachine gm = lookupMachine(gumballMachineId);
		if ( gm.getPrice() == money ) {
			Gumball g = gm.purchase(money);
			purchaseService.logPurchase(g.getId(), userId);
			return g;
		}
		
		throw new IllegalArgumentException("Exact change only, please."); 
	}
	
	public Double extractCoins(Long gumballMachineId, Long machineKeyId) {
		GumballMachine gm = machineDao.find(gumballMachineId);
		MachineKey mk = keyDao.find(machineKeyId);
		if ( gm.accept(mk) ) {
			return gm.removeMoney();
		}
		
		throw new IllegalArgumentException("Key doesn't fit");
	}
	
	public void addGumballs(GumballMachine gm, List<Gumball> candy) {
		
	}
	
	public List<Gumball> peekAtMachine(GumballMachine gm) {
		return null;
	}
	
	public GumballMachine lookupMachine(Long gumballMachineId) {
		return null;
	}
}
