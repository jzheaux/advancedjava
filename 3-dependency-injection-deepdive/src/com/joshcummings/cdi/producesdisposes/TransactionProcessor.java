package com.joshcummings.cdi.producesdisposes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Named
public class TransactionProcessor {

	/**
	 * Called when the particular scope begins
	 * 
	 * @return
	 */
	@Produces
	@Transactions
	public ExecutorService create() {
	    return Executors.newFixedThreadPool(100);
	}

	/**
	 * Called when the particular scope ends.
	 * 
	 * @param es
	 */
	public void close(@Disposes @Transactions ExecutorService es) {
	    es.shutdown();
	}
}
