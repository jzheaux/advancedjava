package com.joshcummings.di.gumballs;

import java.util.List;

public class GumballMachineService {
	private /*final*/ GumballMachineDao machineDao;
	private /*final*/ MachineKeyDao keyDao;
	private /*final*/ PurchaseService purchaseService;

/*
 	public GumballMachineService(GumballMachineDao machineDao,
			MachineKeyDao keyDao, PurchaseService purchaseService) {
		this.machineDao = machineDao;
		this.keyDao = keyDao;
		this.purchaseService = purchaseService;
	}
//*/

	
	public Gumball purchaseGumball(Long gumballMachineId, Double money, Long userId) {
		GumballMachine gm = lookupMachine(gumballMachineId);
		if ( gm.getPrice() == money ) {
			Gumball g = gm.purchase(money);
			purchaseService.logPurchase(g.getId(), userId);
			return g;
		}
		
		throw new IllegalArgumentException("Exact change only, please."); 
	}
	
	public void setMachineDao(GumballMachineDao machineDao) {
		this.machineDao = machineDao;
	}

	public void setKeyDao(MachineKeyDao keyDao) {
		this.keyDao = keyDao;
	}

	public void setPurchaseService(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
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
